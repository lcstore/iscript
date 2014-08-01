package com.lezo.iscript.yeam.config;

import java.util.UUID;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
		addCookie();
		String refer = url;
		Object bObject = task.get("bid");
		if (bObject != null) {
			String bString = bObject.toString();
			if (bString.indexOf("http:") >= 0) {
				refer = bString;
			}
		}
		HttpGet get = new HttpGet(url);
		get.addHeader("Refer", refer);
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
		oElements = dom.select("#companyName[value]");
		if (!oElements.isEmpty()) {
			JSONUtils.put(itemObject, "shopName", oElements.first().attr("value"));
		}
		oElements = dom.select("#mod_salesvolume p strong");
		if (!oElements.isEmpty()) {
			Integer soldNum = JSONUtils.get(dObject, "soldNum");
			try {
				soldNum = Integer.valueOf(oElements.first().ownText().trim());
			} catch (Exception e) {
				e.printStackTrace();
			}
			JSONUtils.put(itemObject, "soldNum", soldNum);
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

	private void addCookie() {
		BasicClientCookie cookie = new BasicClientCookie("__utma",
				"40580330.1541470702.1396602044.1406527175.1406603327.18");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmc", "193324902");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmz",
				"193324902.1401026096.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("provinceId", "1");
		client.getCookieStore().addCookie(cookie);
		String[] uArr = UUID.randomUUID().toString().split("-");
		cookie = new BasicClientCookie("uname", uArr[0]);
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("yihaodian_uid", "" + Math.abs(uArr[0].hashCode()));
		client.getCookieStore().addCookie(cookie);
	}
}
