package com.lezo.iscript.yeam.config;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpClientFactory;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class Config1688Category implements ConfigParser {
	private DefaultHttpClient client = HttpClientFactory.createHttpClient();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String url = "http://www.1688.com/";

		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get, "gbk");
		Document dom = Jsoup.parse(html, url);
		Elements ctElements = dom.select("#industry-layout div.mod-ind i[data-vm]");
		JSONArray ctArray = new JSONArray();
		if (!ctElements.isEmpty()) {
			int size = ctElements.size();
			for (int i = 0; i < size; i++) {
				String sUrl = String
						.format("http://www.1688.com/cmsinclude/%s.html", ctElements.get(i).attr("data-vm"));
				get = new HttpGet(sUrl);
				String chtml = HttpClientUtils.getContent(client, get, "gbk");
				Document cdom = Jsoup.parse(chtml, sUrl);
				Elements rootAs = cdom.select("div.header h1.title.ms-yh a[href]");
				if (rootAs.isEmpty()) {
					continue;
				}
				JSONObject rootObject = new JSONObject();
				JSONUtils.put(rootObject, "name", rootAs.first().ownText());
				JSONUtils.put(rootObject, "url", rootAs.first().absUrl("href"));
				ctArray.put(rootObject);

				Elements ct2As = cdom.select("div.content div.category.mod-spm div.cat-cell h3.cat-title a[href]");
				if (ct2As.isEmpty()) {
					continue;
				}
				JSONArray ct2Array = new JSONArray();
				JSONUtils.put(rootObject, "children", ct2Array);
				for (int j = 0; j < ct2As.size(); j++) {
					JSONObject ct2Object = new JSONObject();
					JSONUtils.put(ct2Object, "name", ct2As.get(j).ownText());
					JSONUtils.put(ct2Object, "url", ct2As.get(j).absUrl("href"));
					ct2Array.put(ct2Object);
					Elements ct3As = ct2As.get(j).parent().parent().select("ul li a[href]");
					if (ct3As.isEmpty()) {
						continue;
					}
					JSONArray ct3Array = new JSONArray();
					JSONUtils.put(ct2Object, "children", ct3Array);
					for (int k = 0; k < ct3As.size(); k++) {
						JSONObject ct3Object = new JSONObject();
						JSONUtils.put(ct3Object, "name", ct3As.get(k).ownText());
						JSONUtils.put(ct3Object, "url", ct3As.get(k).absUrl("href"));
						ct3Array.put(ct3Object);
					}
				}
			}
		}
		return ctArray.toString();
	}
}
