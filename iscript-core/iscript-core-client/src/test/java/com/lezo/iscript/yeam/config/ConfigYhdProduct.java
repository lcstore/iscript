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

public class ConfigYhdProduct implements ConfigParser {
	private DefaultHttpClient client = HttpRequestManager.getDefaultManager().getClient();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String url = task.get("url").toString();

		HttpGet get = new HttpGet(url);
		get.addHeader("Refer", url);
		String html = HttpClientUtils.getContent(client, get, "UTF-8");
		Document dom = Jsoup.parse(html, url);
		Elements oHomeAs = dom.select("div.layout_wrap.crumbbox div.crumb");
		JSONObject itemObject = new JSONObject();
		if (oHomeAs.isEmpty()) {
			JSONUtils.put(itemObject, "stockNum", -1);
			return itemObject.toString();
		}
		JSONUtils.put(itemObject, "productUrl", url);
		Elements oElements = dom.select("div.main_info_con div[class^=pd] h2,#productMainName");
		if (!oElements.isEmpty()) {
			JSONUtils.put(itemObject, "productName", oElements.first().text());
		}
		oElements = dom.select("#productMercantId[value]");
		if (!oElements.isEmpty()) {
			JSONUtils.put(itemObject, "productCode", oElements.first().attr("value"));
		}
		String detailUrl = String.format(
				"http://gps.yihaodian.com/restful/detail?mcsite=1&provinceId=1&pmId=%s&callback=jsonp%s",
				JSONUtils.getString(itemObject, "productCode"), System.currentTimeMillis());
		HttpGet dGet = new HttpGet(detailUrl);
		dGet.addHeader("Refer", url);
		html = HttpClientUtils.getContent(client, dGet, "UTF-8");
		int fromIndex = html.indexOf("(");
		int toIndex = html.indexOf(")");
		fromIndex = fromIndex < 0 ? 0 : fromIndex;
		toIndex = toIndex < 0 ? 0 : html.length();
		html = html.substring(fromIndex + 1, toIndex);
		JSONObject dObject = new JSONObject(html);
		JSONUtils.put(itemObject, "productPrice", JSONUtils.get(dObject, "currentPrice"));
		JSONUtils.put(itemObject, "yhdPrice", JSONUtils.get(dObject, "yhdPrice"));
		JSONUtils.put(itemObject, "promotPrice", JSONUtils.get(dObject, "promPrice"));
		JSONUtils.put(itemObject, "marketPrice", JSONUtils.get(dObject, "marketPrice"));
		JSONUtils.put(itemObject, "stockNum", JSONUtils.get(dObject, "currentStockNum"));
		JSONUtils.put(itemObject, "soldNum", JSONUtils.get(dObject, "soldNum"));
		oElements = dom.select("#companyName[value]");
		if (!oElements.isEmpty()) {
			JSONUtils.put(itemObject, "shopName", oElements.first().attr("value"));
		}
		oElements = dom.select("#merchantId[value]");
		if (!oElements.isEmpty()) {
			String merchantId = oElements.first().attr("value");
			String shopUrl = "1".equals(merchantId) ? "http://www.yhd.com/" : String.format(
					"http://shop.yhd.com/m-%s.html", merchantId);
			JSONUtils.put(itemObject, "shopUrl", shopUrl);
		}
		oElements = dom.select("#brandName[value]");
		if (!oElements.isEmpty()) {
			JSONUtils.put(itemObject, "brandName", oElements.first().attr("value"));
		}
		String mUrl = String.format(
				"http://e.yhd.com/front-pe/queryNumsByPm.do?pmInfoId=%s&callback=detailSkuPeComment.countCallback",
				JSONUtils.getString(itemObject, "productCode"));
		HttpGet mGet = new HttpGet(mUrl);
		dGet.addHeader("Refer", url);
		html = HttpClientUtils.getContent(client, mGet, "UTF-8");
		try {
			fromIndex = html.indexOf("(");
			toIndex = html.indexOf(")");
			fromIndex = fromIndex < 0 ? 0 : fromIndex;
			toIndex = toIndex < 0 ? 0 : html.length();
			html = html.substring(fromIndex + 1, toIndex);
			JSONObject mObject = new JSONObject(html);
			JSONUtils.put(itemObject, "commentNum", JSONUtils.get(mObject, "experienceNum"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemObject.toString();
	}
}
