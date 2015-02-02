package com.lezo.iscript.yeam.config;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigYhdCategory implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String url = "http://www.yhd.com/marketing/allproduct.html";

		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get, "gbk");
		Document dom = Jsoup.parse(html, url);
		Elements elements = dom.select("div.alonesort div.mt h3 a[href]");
		System.out.println(elements);
		JSONArray listArray = new JSONArray();
		for (Element e : elements) {
			// if (!e.ownText().equals("进口食品、进口牛奶")) {
			// continue;
			// }
			JSONObject cObject = new JSONObject();
			JSONUtils.put(cObject, "url", e.absUrl("href"));
			JSONUtils.put(cObject, "name", e.ownText());
			listArray.put(cObject);
			Elements ct2Elements = e.parent().parent().parent().select("div.mc dl.fore dt a[href]");
			if (ct2Elements.isEmpty()) {
				continue;
			}
			JSONArray cArray = new JSONArray();
			JSONUtils.put(cObject, "children", cArray);
			for (Element ct2Element : ct2Elements) {
				JSONObject c2Object = new JSONObject();
				String sUrl = ct2Element.absUrl("href");
				JSONUtils.put(c2Object, "url", unifyUrl(sUrl));
				JSONUtils.put(c2Object, "name", ct2Element.ownText());
				cArray.put(c2Object);
				Elements ct3Elements = ct2Element.parent().parent().select("dd em span a[href]");
				if (ct3Elements.isEmpty()) {
					continue;
				}
				JSONArray c3Array = new JSONArray();
				JSONUtils.put(c2Object, "children", c3Array);
				for (Element e3 : ct3Elements) {
					JSONObject c3Object = new JSONObject();
					JSONUtils.put(c3Object, "url", unifyUrl(e3.absUrl("href")));
					JSONUtils.put(c3Object, "name", e3.ownText());
					c3Array.put(c3Object);
				}
			}
		}
		return listArray.toString();
	}

	private String unifyUrl(String sUrl) {
		return sUrl = sUrl.replace("|/", "");
	}
}
