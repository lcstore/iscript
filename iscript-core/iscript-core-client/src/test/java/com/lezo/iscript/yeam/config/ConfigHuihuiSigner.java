package com.lezo.iscript.yeam.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigHuihuiSigner implements ConfigParser {
	private DefaultHttpClient client = HttpClientUtils.createHttpClient();

	private String getLocationUrl(Document dom) {
		Elements elements = dom.select("script[language=JavaScript]");

		for (int i = 0; i < elements.size(); i++) {
			String sHtml = elements.get(i).html();
			String getUrl = getLocationUrl(sHtml);
			if (getUrl != null) {
				return getUrl;
			}
		}
		return null;
	}

	private String getLocationUrl(String sHtml) {
		Pattern oReg = Pattern.compile("http://reg.huihui.cn/crossdomain.jsp.*?loginCookie.*?login");
		Matcher matcher = oReg.matcher(sHtml);
		if (matcher.find()) {
			return matcher.group();
		}
		oReg = Pattern.compile("http://www.huihui.cn/activate.*?username.*?login");
		matcher = oReg.matcher(sHtml);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	private HttpEntity getPostEntity(String username, String password) throws Exception {
		String url = "http://www.huihui.cn/activate?url=http%3A%2F%2Fwww.huihui.cn%2Flogin";
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("domains", "huihui.cn"));
		nvPairs.add(new BasicNameValuePair("product", "huihui"));
		nvPairs.add(new BasicNameValuePair("savelogin", "1"));
		nvPairs.add(new BasicNameValuePair("type", "1"));
		nvPairs.add(new BasicNameValuePair("url", url));
		nvPairs.add(new BasicNameValuePair("username", username));
		nvPairs.add(new BasicNameValuePair("password", password));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}

	@Override
	public String getName() {
		return "huihui-sign";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String username = (String) task.get("user");
		String password = (String) task.get("pwd");
		String postUrl = "https://reg.163.com/logins.jsp";

		HttpPost post = new HttpPost();
		post.setURI(new URI(postUrl));
		post.addHeader("Referer", "http://www.huihui.cn/login");
		HttpEntity postEntity = getPostEntity(username, password);
		post.setEntity(postEntity);
		HttpResponse res = client.execute(post);
		String html = EntityUtils.toString(res.getEntity(), "UTF-8");
		post.abort();
		Document dom = Jsoup.parse(html, postUrl);
		String getUrl = getLocationUrl(dom);
		if (getUrl == null) {
			Elements oInfoAs = dom.select("#eHint.info");
			if (!oInfoAs.isEmpty()) {
				return oInfoAs.text();
			}
		}
		HttpGet get = new HttpGet(getUrl);
		res = client.execute(get);
		html = EntityUtils.toString(res.getEntity(), "UTF-8");
		get.abort();
		dom = Jsoup.parse(html, getUrl);
		getUrl = getLocationUrl(dom);
		get = new HttpGet(getUrl);
		html = HttpClientUtils.getContent(client, get);
		String uinfoUrl = "http://www.huihui.cn/u/info.json?_=" + System.currentTimeMillis();
		get = new HttpGet(uinfoUrl);
		html = HttpClientUtils.getContent(client, get);
		Pattern oReg = Pattern.compile("http.*?www.huihui.cn/u/info.json\\?_=[0-9]+");
		Matcher matcher = oReg.matcher(html);
		if (matcher.find()) {
			get = new HttpGet(matcher.group());
			html = HttpClientUtils.getContent(client, get);
			System.out.println("login,2th,uinfo:" + html);
		} else {
			System.out.println("login,1th,uinfo:" + html);
		}

		String checkinUrl = "http://www.huihui.cn/checkin";
		post = new HttpPost(checkinUrl);
		html = HttpClientUtils.getContent(client, post);
		task.getArgs().remove("pwd");

		JSONObject dataObject = new JSONObject();
		JSONArray dArray = new JSONArray();
		JSONUtils.put(dataObject, "dataList", dArray);
		dArray.put(html);
		doCollect(dataObject, task);
		JSONObject rsObject = new JSONObject();
		rsObject.put("rs", html);

		rsObject.put("args", new JSONObject(task.getArgs()));
		return rsObject.toString();
	}

	private void doCollect(JSONObject dataObject, TaskWritable task) {
		JSONObject gObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);
		JSONUtils.put(gObject, "args", argsObject);
		JSONUtils.put(gObject, "rs", dataObject.toString());
		System.err.println("dataObject:" + dataObject);
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}
}
