package com.lezo.iscript.yeam.config;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.envjs.EnvjsUtils;
import com.lezo.iscript.utils.encrypt.Base64Decryptor;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ProxyCollector implements ConfigParser {

	@Override
	public String getName() {
		return "zyue-sign";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String url = (String) task.get("url");
		DefaultHttpClient client = HttpClientUtils.createHttpClient();

		BasicClientCookie cookie = new BasicClientCookie("__utma",
				"193324902.1016719687.1401026096.1401026096.1401026096.1");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmc", "193324902");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmz",
				"193324902.1401026096.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
		client.getCookieStore().addCookie(cookie);

		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get);
		System.out.println(html);
		System.out.println("=========================");
		html = decode(html);
		Pattern oReg = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)[^0-9]+?([0-9]+)", Pattern.MULTILINE);
		Matcher matcher = oReg.matcher(html);
		System.out.println(html);
		while (matcher.find()) {
			System.out.println(matcher.group(1) + ":" + matcher.group(2));
		}
		// Document dom = Jsoup.parse(html, get.getURI().toString());
		// Elements elements = dom.select("div#main table tr");
		// for (Element element : elements) {
		// Elements tdElements = element.select("td");
		// if (tdElements.size() < 2) {
		// continue;
		// }
		// String script = tdElements.first().outerHtml();
		// String sPort = tdElements.get(1).ownText();
		// System.out.println(script + ":" + sPort);
		// }
		JSONObject rsObject = new JSONObject();
		String result = "";
		rsObject.put("rs", result);
		task.getArgs().remove("pwd");
		rsObject.put("args", new JSONObject(task.getArgs()));

		return rsObject.toString();
	}

	private String decode(String html) throws Exception {
		Pattern oReg = Pattern.compile("Base64.decode\\s*\\(.*?([0-9a-zA-Z=]+).*?\\)");
		Matcher matcher = oReg.matcher(html);
		Base64Decryptor decryptor = new Base64Decryptor();
		while (matcher.find()) {
			String ipEncode = matcher.group(1);
			System.out.println(ipEncode);
			String ipDecode = decryptor.decript(ipEncode.getBytes());
			html = html.replace(matcher.group(), ipDecode);
			// matcher.replaceFirst(ipDecode);
		}
		return html;
	}
}
