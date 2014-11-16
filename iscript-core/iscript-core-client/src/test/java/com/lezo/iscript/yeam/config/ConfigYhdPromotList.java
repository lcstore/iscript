package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

/**
 * {"data":[],"nexts":[]}
 * 
 * @param task
 * @return
 * @throws Exception
 */
public class ConfigYhdPromotList implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		addCookie();
		JSONObject dataObject = getDataObject(task);
		logData(dataObject);
		doCollect(dataObject, task);
		return dataObject.toString();
	}

	private void logData(JSONObject dataObject) {
		System.err.println("data:" + dataObject);

	}

	private void addCookie() {
		BasicClientCookie cookie = new BasicClientCookie("__utma", "40580330.1541470702.1396602044.1406527175.1406603327.18");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmc", "193324902");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmz", "193324902.1401026096.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("provinceId", "1");
		client.getCookieStore().addCookie(cookie);
		String[] uArr = UUID.randomUUID().toString().split("-");
		cookie = new BasicClientCookie("uname", uArr[0]);
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("yihaodian_uid", "" + Math.abs(uArr[0].hashCode()));
		client.getCookieStore().addCookie(cookie);
	}

	private void doCollect(JSONObject dataObject, TaskWritable task) {
		JSONObject gObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);

		JSONUtils.put(gObject, "args", argsObject);

		JSONUtils.put(gObject, "rs", dataObject.toString());

		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}

	/**
	 * {"data":[],"nexts":[]}
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private JSONObject getDataObject(TaskWritable task) throws Exception {
		String url = task.get("url").toString();
		url = doParamEncode(url);
		System.err.println("url:" + url);
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", url);
		get.addHeader("DNT", "1");
		String html = HttpClientUtils.getContent(client, get);
		ResultBean rsBean = new ResultBean();
		Document dom = getDocument(html, url);
		addProducts(dom, rsBean);
		addActs(dom, rsBean);
		addNexts(dom, rsBean);
		if (rsBean.getDataList().isEmpty()) {
			System.err.println(html);
		}
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	private Document getDocument(String html, String url) throws Exception {
		Document dom = Jsoup.parse(html, url);
		Elements scriptEls = dom.select("#userSrc ~ script");
		if (scriptEls.isEmpty()) {
			return dom;
		}
		String script = null;
		for (Element ele : scriptEls) {
			String sHtml = ele.html();
			if (sHtml.indexOf("frames['userSrc']") > 0) {
				script = sHtml;
				break;
			}
		}
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();
		String source = "var frames={};frames['userSrc']={};" + script;
		cx.evaluateString(scope, source, "", 0, null);
		String directUrl = Context.toString(ScriptableObject.getProperty(scope, "url"));
		System.err.println("get directUrl:" + directUrl);
		HttpGet get = new HttpGet(directUrl);
		get.addHeader("Referer", url);
		get.addHeader("DNT", "1");
		html = HttpClientUtils.getContent(client, get);
		scope= null;
		return Jsoup.parse(html, url);
	}

	private String doParamEncode(String url) throws Exception {
		url = url.replace("|", "%7C");
		return url;
	}

	private void addProducts(Document dom, ResultBean rsBean) {
		Elements productAs = dom.select("a[href*=item.yhd.com/item/],[a[href*=t.yhd.com/detail]");
		if (productAs.isEmpty()) {
			return;
		}
		int size = productAs.size();
		Set<String> urlSet = new HashSet<String>(size);
		List<Object> dataList = rsBean.getDataList();
		for (int i = 0; i < size; i++) {
			String pUrl = productAs.get(i).absUrl("href");
			if (!urlSet.contains(pUrl)) {
				dataList.add(pUrl);
				urlSet.add(pUrl);
			}
		}

	}

	private void addNexts(Document dom, ResultBean rsBean) {

	}

	private void addActs(Document dom, ResultBean rsBean) {
		String url = dom.baseUri();
		Elements actAs = dom.select("#cms_first_dom a[href*=http://cms.yhd.com/cmsPage/show.do][target],#cms_first_dom map area[href*=http://cms.yhd.com/cmsPage/show.do]");
		if (actAs.isEmpty()) {
			return;
		}
		Pattern oReg = Pattern.compile("pageId=[0-9]+");
		Matcher matcher = oReg.matcher(url);
		String curPageId = matcher.find() ? matcher.group() : "";
		int len = actAs.size();
		Set<String> urlSet = new HashSet<String>(len);
		urlSet.add(dom.baseUri());
		String head = "url=";
		List<Object> nextList = rsBean.getNextList();
		for (int i = 0; i < len; i++) {
			String sActUrl = actAs.get(i).absUrl("href");
			int index = sActUrl.indexOf(head);
			sActUrl = index > 0 ? sActUrl.substring(index + head.length()) : sActUrl;
			if (!sActUrl.contains(curPageId) && !urlSet.contains(sActUrl)) {
				nextList.add(sActUrl);
				urlSet.add(sActUrl);
			}
		}
	}

	private final class ResultBean {
		private List<Object> dataList = new ArrayList<Object>();
		private List<Object> nextList = new ArrayList<Object>();

		public List<Object> getDataList() {
			return dataList;
		}

		public void setDataList(List<Object> dataList) {
			this.dataList = dataList;
		}

		public List<Object> getNextList() {
			return nextList;
		}

		public void setNextList(List<Object> nextList) {
			this.nextList = nextList;
		}

	}
}