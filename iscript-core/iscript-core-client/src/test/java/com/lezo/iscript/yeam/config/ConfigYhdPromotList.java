package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
		JSONObject dataObject = getDataObject(task);
		logData(dataObject);
		doCollect(dataObject, task);
		return dataObject.toString();
	}

	private void logData(JSONObject dataObject) {
		System.err.println("data:" + dataObject);

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
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get);
		ResultBean rsBean = new ResultBean();
		Document dom = Jsoup.parse(html, url);
		addProducts(dom, rsBean);
		addActs(dom, rsBean);
		addNexts(dom, rsBean);
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	private void addProducts(Document dom, ResultBean rsBean) {
		Elements productAs = dom.select("a[href*=item.yhd.com][target]");
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
		if (url.indexOf("cms.yhd.com/cmsPage/show.do") > -1) {
			return;
		}
		Elements actAs = dom.select("a[href*=http://cms.yhd.com/cmsPage/show.do][target]");
		if (actAs.isEmpty()) {
			return;
		}
		int len = actAs.size();
		Set<String> urlSet = new HashSet<String>(len);
		urlSet.add(dom.baseUri());
		String head = "url=";
		List<Object> nextList = rsBean.getNextList();
		for (int i = 0; i < len; i++) {
			String sActUrl = actAs.get(i).absUrl("href");
			int index = sActUrl.indexOf(head);
			sActUrl = index > 0 ? sActUrl.substring(index + head.length()) : sActUrl;
			if (!urlSet.contains(sActUrl)) {
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