package com.lezo.iscript.yeam.config;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigYhdList implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String url = task.get("url").toString();
		url = turnUrl(url);
		System.err.println(url);
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get, "UTF-8");
		html = turnHtml(html);
		Document dom = Jsoup.parse(html, url);

		JSONObject listObject = new JSONObject();
		JSONArray listArray = new JSONArray();
		JSONUtils.put(listObject, "list", listArray);

		addListArray(dom, listArray);
		addNextUrls(dom, listObject);
		return listObject.toString();
	}

	private String turnHtml(String html) {
		if (html.startsWith("jsonp")) {
			int beginIndex = html.indexOf("(");
			int endIndex = html.lastIndexOf(")");
			html = html.substring(beginIndex + 1, endIndex);
		}
		JSONObject sObject = JSONUtils.getJSONObject(html);
		if (sObject == null) {
			return html;
		}
		return JSONUtils.getString(sObject, "value");
	}

	private String turnUrl(String url) {
		if (url.endsWith("|")) {
			url = url.substring(0, url.length() - 1);
		}
		if (url.indexOf("www.yhd.com/ctg/searchPage") > 0) {
			return url;
		}
		Pattern qReg = Pattern.compile("c[0-9]+-.*(k.*?/)?");
		Matcher matcher = qReg.matcher(url);
		if (matcher.find()) {
			String query = matcher.group();
			url = String.format("http://www.yhd.com/ctg/searchPage/%s", query);
		} else {
			qReg = Pattern.compile("vc[0-9]+.*");
			matcher = qReg.matcher(url);
			if (matcher.find()) {
				String query = matcher.group();
				url = String.format("http://www.yhd.com/ctg/searchVirCateAjax/%s", query);
				url = url.replace("/c0/", "/");
				url = url.replace("/b/", "/c0/b/");
			}
		}
		Pattern sReg = Pattern.compile("a-([a-zA-Z0-9]+-)+[a-zA-Z0-9]+");
		matcher = sReg.matcher(url);
		if (matcher.find()) {
			Pattern nReg = Pattern.compile("-p[0-9]+-");
			Matcher nMatcher = nReg.matcher(url);
			String sort = "a-s2-v0-p1-price-d0-f0-m1-rt0-pid-mid0-k";
			if (nMatcher.find()) {
				sort = sort.replace("-p1-", nMatcher.group());
			}
			url = matcher.replaceFirst(sort);
		} else {
			url = url.endsWith("/") ? url : url + "/";
			if (url.indexOf("/k") > 0) {
				url = url.replace("/k", "/b/a-s2-v0-p1-price-d0-f0-m1-rt0-pid-mid0-k");
			} else {
				url += "b/a-s2-v0-p1-price-d0-f0-m1-rt0-pid-mid0-k";
			}
		}
		if (!url.contains("?callback=jsonp")) {
			url += "?callback=jsonp" + System.currentTimeMillis();
		}
		if (url.indexOf("searchVirCateAjax") > 0) {
			url = url.replace("-price-d0-f0-m1-rt0-pid-mid0-k", "-price-d0-mid0-f0");
		}
		return url;
	}

	private void addNextUrls(Document dom, JSONObject listObject) {
		String url = dom.baseUri();
		Elements curPageAs = dom.select("#turnPageBottom.turn_page span.page_cur");
		if (curPageAs.isEmpty()) {
			JSONArray logArray = new JSONArray();
			JSONUtils.put(listObject, "logs", logArray);
			logArray.put("Get 0 next page url...");
			return;
		}
		String sCurPage = curPageAs.first().ownText();
		Integer iCurPage = Integer.valueOf(sCurPage);
		if (iCurPage != 1) {
			return;
		}
		JSONArray nextArray = new JSONArray();
		JSONUtils.put(listObject, "nexts", nextArray);
		Elements pageCoutAs = dom.select("#pageCountPage[value]");
		if (!pageCoutAs.isEmpty()) {
			int count = Integer.valueOf(pageCoutAs.first().attr("value"));

			if (url.indexOf("-p1-") > 0) {
				int index = url.indexOf("?");
				index = index < 0 ? url.length() : index;
				String listHeader = url.substring(0, index);
				for (int i = 2; i <= count; i++) {
					String sNext = listHeader.replace("-p1-", "-p" + i + "-");
					sNext += "?callback=jsonp" + System.currentTimeMillis();
					nextArray.put(sNext);
				}
			} else {
				System.err.println("Offer next page,but url:" + url);
			}
		}
	}

	private void addListArray(Document dom, JSONArray listArray) throws Exception {
		Elements ctElements = dom.select("li[id^=producteg_]");
		if (ctElements.isEmpty()) {
			return;
		}
		int size = ctElements.size();
		StringBuilder sb = new StringBuilder();
		Elements jValueEls = dom.select("#jsonValue[value]");
		int pageRanking = 0;
		if (!jValueEls.isEmpty()) {
			int pageSize = 36;
			String jValue = jValueEls.first().attr("value");
			JSONObject jObject = JSONUtils.getJSONObject(jValue);
			Integer currentPage = JSONUtils.getInteger(jObject, "currentPage");
			pageRanking = (currentPage - 1) * pageSize;
		}
		for (int i = 0; i < size; i++) {
			JSONObject itemObject = new JSONObject();
			listArray.put(itemObject);
			JSONUtils.put(itemObject, "ranking", pageRanking + listArray.length());
			JSONUtils.put(itemObject, "sortType", 0);
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
			Elements oImgAs = ctElements.get(i).select("a[id^=pdlink1_].search_prod_img img[src]");
			if (!oImgAs.isEmpty()) {
				String sImgUrl = oImgAs.first().absUrl("src");
				JSONUtils.put(itemObject, "imgUrl", sImgUrl);
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
