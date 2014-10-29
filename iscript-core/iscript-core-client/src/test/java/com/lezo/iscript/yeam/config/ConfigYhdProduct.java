package com.lezo.iscript.yeam.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigYhdProduct implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static Map<String, String> hostIpMap = new HashMap<String, String>();
	private static final String EMTPY_RESULT = new JSONObject().toString();
	static {
		hostIpMap.put("item.yhd.com", "180.153.252.38");
		hostIpMap.put("gps.yihaodian.com", "180.153.252.46");
		hostIpMap.put("e.yhd.com", "180.153.252.36");
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		addCookie();
		JSONObject itemObject = getItemObject(task);
		doCollect(itemObject, task);
		return EMTPY_RESULT;
	}

	private void doCollect(JSONObject itemObject, TaskWritable task) {
		JSONObject gObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);

		JSONUtils.put(gObject, "args", argsObject);

		// {"target":[],"data":[],"nexts":[]}
		JSONObject dataObject = new JSONObject();
		JSONArray tArray = new JSONArray();
		tArray.put("ProductDto");
		tArray.put("ProductStatDto");
		JSONUtils.put(dataObject, "target", tArray);
		JSONArray dArray = new JSONArray();
		dArray.put(itemObject);
		JSONUtils.put(dataObject, "dataList", dArray);
		System.err.println(dataObject);

		JSONUtils.put(gObject, "rs", dataObject.toString());

		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}

	private JSONObject getItemObject(TaskWritable task) throws Exception {
		String url = (String) task.get("url");
		String refer = url;
		HttpGet get = createHttpGetWithIp(url);
		get.addHeader("Refer", refer);
		String html = HttpClientUtils.getContent(client, get, "UTF-8");
		Document dom = Jsoup.parse(html, url);
		Elements oHomeAs = dom.select("div.layout_wrap.crumbbox div.crumb");
		JSONObject itemObject = new JSONObject();
		if (oHomeAs.isEmpty()) {
			JSONUtils.put(itemObject, "stockNum", -1);
			return itemObject;
		}
		String barCode = (String) task.get("barCode");
		if (BarCodeUtils.isBarCode(barCode)) {
			JSONUtils.put(itemObject, "barCode", barCode);
		}
		JSONUtils.put(itemObject, "siteId", 1002);
		JSONUtils.put(itemObject, "productUrl", url);
		Elements oElements = dom.select("div.main_info_con div[class^=pd] h2,#productMainName");
		if (!oElements.isEmpty()) {
			JSONUtils.put(itemObject, "productName", oElements.first().text());
		}
		oElements = dom.select("#productMercantId[value]");
		if (!oElements.isEmpty()) {
			JSONUtils.put(itemObject, "productCode", oElements.first().attr("value"));
		}
		String detailUrl = String.format("http://gps.yihaodian.com/restful/detail?mcsite=1&provinceId=1&pmId=%s&callback=jsonp%s", JSONUtils.getString(itemObject, "productCode"), System.currentTimeMillis());
		HttpGet dGet = createHttpGetWithIp(detailUrl);
		dGet.addHeader("Refer", url);
		html = HttpClientUtils.getContent(client, dGet, "UTF-8");
		int fromIndex = html.indexOf("(");
		int toIndex = html.indexOf(")");
		fromIndex = fromIndex < 0 ? 0 : fromIndex;
		toIndex = toIndex < 0 ? 0 : html.length();
		html = html.substring(fromIndex + 1, toIndex);
		JSONObject dObject = new JSONObject(html);
		JSONUtils.put(itemObject, "productPrice", JSONUtils.getFloat(dObject, "currentPrice"));
		JSONUtils.put(itemObject, "yhdPrice", JSONUtils.getFloat(dObject, "yhdPrice"));
		JSONUtils.put(itemObject, "promotPrice", JSONUtils.getFloat(dObject, "promPrice"));
		JSONUtils.put(itemObject, "marketPrice", JSONUtils.getFloat(dObject, "marketPrice"));
		JSONUtils.put(itemObject, "stockNum", JSONUtils.getInteger(dObject, "currentStockNum"));
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
		} else {
			oElements = dom.select("#mod_salesvolume[saleNumber]");
			if (!oElements.isEmpty()) {
				JSONUtils.put(itemObject, "soldNum", Integer.valueOf(oElements.first().attr("saleNumber")));
			}
		}
		oElements = dom.select("div.crumb a[href^=http://www.yhd.com/ctg/],div.crumb a[href^=http://list.yhd.com/]");
		if (!oElements.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Element oEle : oElements) {
				if (sb.length() < 1) {
					sb.append(oEle.ownText());
				} else {
					sb.append(";");
					sb.append(oEle.ownText());
				}
			}
			JSONObject attrObject = JSONUtils.get(itemObject, "attrs");
			if (attrObject == null) {
				attrObject = new JSONObject();
				JSONUtils.put(itemObject, "attrs", attrObject);
			}
			String sCat = sb.toString();
			JSONUtils.put(attrObject, "categorys", sCat);
			JSONUtils.put(itemObject, "categoryNav", sCat);
		}
		oElements = dom.select("#prodDetailCotentDiv.desitem dl.des_info dd[title]");
		if (!oElements.isEmpty()) {
			JSONArray descArray = new JSONArray();
			for (Element oEle : oElements) {
				descArray.put(oEle.attr("title"));
			}
			JSONObject attrObject = JSONUtils.get(itemObject, "attrs");
			if (attrObject == null) {
				attrObject = new JSONObject();
				JSONUtils.put(itemObject, "attrs", attrObject);
			}
			JSONUtils.put(attrObject, "descriptions", descArray);
		}
		oElements = dom.select("#merchantId[value]");
		if (!oElements.isEmpty()) {
			String merchantId = oElements.first().attr("value");
			String shopUrl = "1".equals(merchantId) ? "http://www.yhd.com/" : String.format("http://shop.yhd.com/m-%s.html", merchantId);
			JSONUtils.put(itemObject, "shopUrl", shopUrl);
			JSONUtils.put(itemObject, "shopCode", merchantId);
		}
		oElements = dom.select("#brandName[value]");
		if (!oElements.isEmpty()) {
			JSONUtils.put(itemObject, "brandName", oElements.first().attr("value"));
		}
		oElements = dom.select("#J_tabSlider ul.imgtab_con li a img[id][src]");
		if (!oElements.isEmpty()) {
			String imgUrl = oElements.first().attr("src");
			imgUrl = imgUrl.replace("_60x60.jpg", "_200x200.jpg");
			JSONUtils.put(itemObject, "imgUrl", imgUrl);
		}
		String mUrl = String.format("http://e.yhd.com/front-pe/queryNumsByPm.do?pmInfoId=%s&callback=detailSkuPeComment.countCallback", JSONUtils.getString(itemObject, "productCode"));
		HttpGet mGet = createHttpGetWithIp(mUrl);
		dGet.addHeader("Refer", url);
		html = HttpClientUtils.getContent(client, mGet, "UTF-8");
		try {
			fromIndex = html.indexOf("(");
			toIndex = html.indexOf(")");
			fromIndex = fromIndex < 0 ? 0 : fromIndex;
			toIndex = toIndex < 0 ? 0 : html.length();
			html = html.substring(fromIndex + 1, toIndex);
			JSONObject mObject = new JSONObject(html);
			JSONUtils.put(itemObject, "commentNum", JSONUtils.getInteger(mObject, "experienceNum"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemObject;
	}

	private HttpGet createHttpGetWithIp(String url) throws Exception {
		URI oUri = new URI(url);
		String host = oUri.getHost();
		String oldUrl = oUri.toString();
		String ip = hostIpMap.get(host);
		if (ip != null) {
			url = oldUrl.replace(host, ip);
		}
		HttpGet get = new HttpGet(url);
		get.addHeader("Host", oUri.getHost());
		return get;
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
}
