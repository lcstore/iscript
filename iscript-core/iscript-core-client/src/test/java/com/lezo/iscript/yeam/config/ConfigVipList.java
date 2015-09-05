package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Scriptable;

import com.lezo.iscript.proxy.ProxyClientUtils;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigVipList implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	public static final Integer SITE_ID = 1001;

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	private void ensureCookie() throws Exception {
		Set<String> checkSet = new HashSet<String>();
		checkSet.add("__jda");
		checkSet.add("__jdb");
		checkSet.add("__jdc");
		checkSet.add("__jdv");
		boolean hasAddCookie = false;
		for (Cookie ck : client.getCookieStore().getCookies()) {
			if (checkSet.contains(ck.getName())) {
				hasAddCookie = true;
				break;
			}
		}
		if (!hasAddCookie) {
			addCookie(client, null);
		}
	}

	private void addCookie(DefaultHttpClient client, Scriptable scope) throws Exception {
		Map<String, String> cookieMap = new HashMap<String, String>();
		cookieMap.put("__jda", "95931165.580577879.1416135846.1416135846.1416135846.1");
		cookieMap.put("__jdb", "95931165.1.580577879|1.1416135846");
		cookieMap.put("__jdc", "95931165");
		cookieMap.put("__jdv", "95931165|direct|-|none|-");
		for (String key : cookieMap.keySet()) {
			String cookieValue = cookieMap.get(key);
			BasicClientCookie cookie = new BasicClientCookie(key, cookieValue);
			cookie.setDomain(".jd.com");
			client.getCookieStore().addCookie(cookie);
		}
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
        // ensureCookie();
		DataBean dataBean = getDataObject(task);
		return convert2TaskCallBack(dataBean, task);
	}

	private String convert2TaskCallBack(DataBean dataBean, TaskWritable task) throws Exception {
		JSONObject returnObject = new JSONObject();
		if (dataBean != null) {
			ObjectMapper mapper = new ObjectMapper();
			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, dataBean);
			String dataString = writer.toString();

			JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, dataString);
			JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
		}
		return returnObject.toString();
	}

	/**
	 * {"dataList":[],"nextList":[]}
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private DataBean getDataObject(TaskWritable task) throws Exception {
		String url = task.get("url").toString();
        url = url.replace("|", "%7C");
		HttpGet get = ProxyClientUtils.createHttpGet(url, task);
		String html = HttpClientUtils.getContent(client, get);
		DataBean dataBean = new DataBean();
        // Document dom = Jsoup.parse(html, url);
        // Elements urlEls = dom.select("#plist a[href~=item.jd.com/[0-9]{5,}.html$]");
        // List<Object> dataList = dataBean.getDataList();
        // Set<String> hasSet = new HashSet<String>();
        // for (Element urlEle : urlEls) {
        // String sUrl = urlEle.absUrl("href");
        // if (!hasSet.contains(sUrl)) {
        // dataList.add(sUrl);
        // hasSet.add(sUrl);
        // }
        // }
        // addNexts(dataBean, dom);
		return dataBean;
	}

	private void addNexts(DataBean dataBean, Document dom) {
		Elements curELs = dom.select("#J_bottomPage.p-wrap span.p-num a.curr[href*=page=1]");
		if (curELs.isEmpty()) {
			return;
		}
		String curPageUrl = curELs.first().absUrl("href");
		Elements totalELs = dom.select("#J_bottomPage.p-wrap span.p-skip em:contains(共) b");
		int total = Integer.valueOf(totalELs.first().ownText());
		List<Object> nextList = dataBean.getNextList();
		for (int i = 2; i <= total; i++) {
			nextList.add(curPageUrl.replace("page=1", "page=" + i));
		}
	}

}