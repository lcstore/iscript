package com.lezo.iscript.yeam.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class LatestSohuNews implements ConfigParser {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	@Override
	public String getName() {
		return "sohunews-latest";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		JSONObject rsObject = getResultObject(task, client);
		return rsObject.toString();
	}

	private JSONObject getResultObject(TaskWritable task, DefaultHttpClient client) throws Exception {
		String curDateString = sdf.format(new Date());
		String postUrl = "http://news.sohu.com/_scroll_newslist/" + curDateString + "/news.inc";

		HttpGet get = new HttpGet(postUrl);
		get.addHeader("Referer", "http://news.sohu.com/scroll/");
		get.addHeader("Accept-Encoding", "gzip, deflate");
		get.addHeader("Accept", "text/javascript, application/javascript, */*");

		String html = HttpClientUtils.getContent(client, get, "UTF-8");
		int index = html.indexOf("{");
		index = index < 0 ? 0 : index;
		html = html.substring(index);
		JSONObject rsObject = new JSONObject();
		rsObject.put("rs", html);
		task.getArgs().remove("pwd");
		rsObject.put("args", new JSONObject(task.getArgs()));
		return rsObject;
	}

}
