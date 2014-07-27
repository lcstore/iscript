package com.lezo.iscript.yeam.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.script.CommonContext;
import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpRequestManager;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigYhdList implements ConfigParser {
	private DefaultHttpClient client = HttpRequestManager.getDefaultManager().getClient();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String url = task.get("url").toString();

		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get, "UTF-8");
		Document dom = Jsoup.parse(html, url);

		JSONObject listObject = new JSONObject();
		JSONArray listArray = new JSONArray();
		JSONUtils.put(listObject, "list", listArray);

		addListArray(dom, listArray);
		Object getNexts = task.get("getNexts");
		if (getNexts != null) {
			JSONArray nextArray = new JSONArray();
			JSONUtils.put(listObject, "nexts", nextArray);
			Elements pageCoutAs = dom.select("#pageCountPage[value]");
			if (!pageCoutAs.isEmpty()) {
				int count = Integer.valueOf(pageCoutAs.first().attr("value"));
				int index = url.indexOf("#page=");
				String listHeader = index < 0 ? url : url.substring(0, index);
				for (int i = 2; i <= count; i++) {
					nextArray.put(String.format("%s#page=%d&sort=2", listHeader, i));
				}
			}
		}
		return listObject.toString();
	}

	private void addListArray(Document dom, JSONArray listArray) {
		Elements ctElements = dom.select("a[id^=pdlink].title[href]");
		if (ctElements.isEmpty()) {
			return;
		}
		int size = ctElements.size();
		for (int i = 0; i < size; i++) {
			JSONObject itemObject = new JSONObject();
			JSONUtils.put(itemObject, "name", ctElements.get(i).text());
			JSONUtils.put(itemObject, "url", ctElements.get(i).absUrl("href"));
			listArray.put(itemObject);
		}
	}
}
