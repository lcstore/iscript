package com.lezo.iscript.yeam.config;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class Latest360News implements ConfigParser {

	@Override
	public String getName() {
		return "163news-latest";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		JSONObject rsObject = getResultObject(task, client);
		return rsObject.toString();
	}

	private JSONObject getResultObject(TaskWritable task, DefaultHttpClient client) throws Exception {
		String postUrl = "http://news.163.com/special/0001220O/news_json.js?" + Math.random();

		HttpGet get = new HttpGet(postUrl);
		get.addHeader("Referer", "http://news.163.com/latest/");

		String html = HttpClientUtils.getContent(client, get,"gbk");
		JSONObject rsObject = new JSONObject();
		rsObject.put("rs", html);
		task.getArgs().remove("pwd");
		rsObject.put("args", new JSONObject(task.getArgs()));
		return rsObject;
	}

}
