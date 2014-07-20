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
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class Config1688List implements ConfigParser {
	private DefaultHttpClient client = HttpClientUtils.createHttpClient();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String url = task.get("url").toString();

		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get, "gbk");
		Document dom = Jsoup.parse(html, url);

		JSONObject listObject = new JSONObject();
		JSONArray listArray = new JSONArray();
		JSONUtils.put(listObject, "list", listArray);

		addListArray(dom, listArray);
		Elements dataEles = dom
				.select("#sw_mod_pagination_content[data-mod-config],#sw_delayload_url[data-mod-config]");
		for (Element dEle : dataEles) {
			String dataString = dEle.attr("data-mod-config");
			JSONObject dObject = JSONUtils.getJSONObject(dataString);
			String loadUrl = JSONUtils.getString(dObject, "url");
			if (StringUtils.isEmpty(loadUrl)) {
				continue;
			}
			HttpGet loadGet = new HttpGet(loadUrl);
			html = HttpClientUtils.getContent(client, loadGet);
			int index = html.indexOf("(");
			html = "var loadHtml = callBack" + html.substring(index);
			Context cx = Context.enter();
			ScriptableObject parent = CommonContext.getCommonScriptable();
			Scriptable scope = cx.initStandardObjects(parent);
			String source = "function callBack(data){return data.content.offerResult.html;}; " + html;
			cx.evaluateString(scope, source, "cmd", 0, null);
			Object loadObject = ScriptableObject.getProperty(scope, "loadHtml");
			html = Context.toString(loadObject);
			Document jDom = Jsoup.parse(html, url);
			addListArray(jDom, listArray);
		}
		JSONArray nextArray = new JSONArray();
		JSONUtils.put(listObject, "nexts", nextArray);
		Elements nextAs = dom.select("a[href].page-next:contains(下一页)");
		if (!nextAs.isEmpty()) {
			nextArray.put(nextAs.first().absUrl("href"));
		}
		return listObject.toString();
	}

	private void addListArray(Document dom, JSONArray listArray) {
		Elements ctElements = dom
				.select("li[id^=offer] h2.sm-offerShopwindow-title a.sm-offerShopwindow-titleLink[href]");
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
