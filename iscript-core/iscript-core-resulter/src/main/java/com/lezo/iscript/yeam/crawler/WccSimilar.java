package com.lezo.iscript.yeam.crawler;

import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class WccSimilar implements ConfigParser {
	private static Logger log = Logger.getLogger(WccSimilar.class);
	private DefaultHttpClient client = HttpClientUtils.createHttpClient();

	public String getName() {
		return "wcc-similar";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject rs = new JSONObject();
		JSONUtils.put(rs, "args", JSONUtils.getJSONObject(task.getArgs()));
		String url = get(task.getArgs(), "wUrl");
		Float price = get(task.getArgs(), "wPrice");
		String name = get(task.getArgs(), "wName");
		log.info("search:" + name);
		url = URLEncoder.encode(url, "UTF-8");
		name = URLEncoder.encode(name, "UTF-8");
		String sUrl = String
				.format("http://e.wochacha.com/Search/productSearch?feedback=true&url=%s&price=%s&name=%s&isbn=&version=3.2.1&vendor=chrome&source=package&wccuid=%s",
						url, price, name, UUID.randomUUID().toString());
		HttpGet get = new HttpGet(sUrl);
		get.addHeader("refer", url);
		String html = HttpClientUtils.getContent(client, get);
		JSONUtils.put(rs, "rs", html);
		return rs.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Map<String, Object> map, String key) {
		if (key == null) {
			return null;
		}
		Object valueObject = map.get(key);
		if (valueObject == null) {
			return null;
		}
		return (T) valueObject;
	}

	public static Integer getInteger(Map<String, Object> map, String key) {
		return get(map, key);
	}
}
