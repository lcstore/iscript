package com.lezo.iscript.yeam.config;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private void addListArray(Document dom, JSONArray listArray) throws Exception {
		Elements ctElements = dom.select("li[id^=producteg_]");
		if (ctElements.isEmpty()) {
			return;
		}
		int size = ctElements.size();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			JSONObject itemObject = new JSONObject();
			listArray.put(itemObject);
			Elements oNameUrlAs = ctElements.get(i).select("a[id^=pdlink].title[href][pmid]");
			if (!oNameUrlAs.isEmpty()) {
				JSONUtils.put(itemObject, "productName", oNameUrlAs.first().text());
				JSONUtils.put(itemObject, "productUrl", oNameUrlAs.first().absUrl("href"));
				JSONUtils.put(itemObject, "productCode", oNameUrlAs.first().attr("pmid"));
			}
			Elements oPriceAs = ctElements.get(i).select("span[id^=price0].price[yhdprice][productid]");
			if (!oPriceAs.isEmpty()) {
				String pid = oPriceAs.first().attr("productid");
				JSONUtils.put(itemObject, "productId", pid);
				sb.append(String.format("&productIds=%s", pid));
			}
			Elements oCmmAs = ctElements.get(i).select("p.comment a[id^=pdlinkcomment_]");
			if (!oCmmAs.isEmpty()) {
				String content = oCmmAs.first().ownText();
				Pattern oReg = Pattern.compile("[0-9]+");
				Matcher matcher = oReg.matcher(content);
				if (matcher.find()) {
					JSONUtils.put(itemObject, "commentNum", matcher.group());
				}
			}
		}
		String bStockUrl = String
				.format("http://busystock.i.yihaodian.com/busystock/restful/truestock?mcsite=1&provinceId=1%s&callback=jsonp%s",
						sb.toString(), System.currentTimeMillis());
		HttpGet sGet = new HttpGet(bStockUrl);
		sGet.addHeader("Referer", dom.baseUri());
		String html = HttpClientUtils.getContent(client, sGet, "UTF-8");
		int fromIndex = html.indexOf("(");
		int toIndex = html.indexOf(")");
		fromIndex = fromIndex < 0 ? 0 : fromIndex;
		toIndex = toIndex < 0 ? 0 : html.length();
		html = html.substring(fromIndex + 1, toIndex);
		JSONArray sArray = new JSONArray(html);
		Map<String, JSONObject> idMap = new HashMap<String, JSONObject>();
		for (int i = 0; i < sArray.length(); i++) {
			JSONObject itemObject = sArray.getJSONObject(i);
			idMap.put(JSONUtils.getString(itemObject, "productId"), itemObject);
		}
		for (int i = 0; i < listArray.length(); i++) {
			JSONObject itemObject = listArray.getJSONObject(i);
			String pid = JSONUtils.getString(itemObject, "productId");
			JSONObject sObject = idMap.get(pid);
			if (sObject == null) {
				System.err.println(itemObject);
			} else {
				JSONUtils.put(itemObject, "productCode", JSONUtils.getObject(sObject, "pmId"));
				JSONUtils.put(itemObject, "stockNum", JSONUtils.getObject(sObject, "productStock"));
				JSONUtils.put(itemObject, "marketPrice", JSONUtils.getObject(sObject, "marketPrice"));
				JSONUtils.put(itemObject, "productPrice", JSONUtils.getObject(sObject, "productPrice"));
				JSONUtils.put(itemObject, "promotPrice", JSONUtils.getObject(sObject, "promPrice"));
				JSONUtils.put(itemObject, "yhdPrice", JSONUtils.getObject(sObject, "yhdPrice"));
			}
		}
	}
}