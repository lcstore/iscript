package com.lezo.iscript.yeam.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class Config1688Product implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(Config1688Product.class);
	private DefaultHttpClient client = HttpClientUtils.createHttpClient();
	private Pattern oReg = Pattern.compile("[0-9]{13,}");

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
		Elements elements = dom.select("div#mod-detail-hd.mod-detail-hd h1.d-title");
		JSONObject itemObject = new JSONObject();
		if (!elements.isEmpty()) {
			JSONUtils.put(itemObject, "name", elements.first().ownText());
		} else {
			logger.warn("can not get name:" + url);
		}

		Elements attrElements = dom.select("div#mod-detail-attributes table tr td.de-feature");
		for (Element ele : attrElements) {
			String attr = ele.ownText();
			int index = attr.indexOf("：");
			index = index < 0 ? attr.indexOf(":") : index;
			String key = index < 0 ? attr : attr.substring(0, index);
			String value = index < 0 ? attr : attr.substring(index + 1);
			key = turnKey(key);
			JSONUtils.put(itemObject, key, value);
		}
		Elements priceElements = dom.select("table.de-price-hd tr[data-range]");
		if (!priceElements.isEmpty()) {
			JSONUtils.put(itemObject, "dataRange", priceElements.first().attr("data-range"));
		}
		Elements imgElements = dom.select("div.vertical-img a.box-img img[src]");
		if (!imgElements.isEmpty()) {
			JSONUtils.put(itemObject, "imgUrl", imgElements.first().absUrl("src"));
		}
		String barCode = JSONUtils.getString(itemObject, "barCode");
		if (!StringUtils.isEmpty(barCode)) {
			Matcher matcher = oReg.matcher(barCode);
			if (matcher.find()) {
				JSONUtils.put(itemObject, "barCode", matcher.group());
			}
		}
		return itemObject.toString();
	}

	private String turnKey(String key) {
		if (Pattern.matches(".*?条.?码.*", key)) {
			return "barCode";
		} else if (Pattern.matches(".*?品.?牌.*", key)) {
			return "brand";
		}
		return key;
	}
}
