package com.lezo.iscript.yeam.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.encrypt.Base64Decryptor;
import com.lezo.iscript.yeam.http.HttpRequestManager;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxyCollector implements ConfigParser {
	private List<String> proxySeedList;

	public ConfigProxyCollector() {
		proxySeedList = new ArrayList<String>();
		proxySeedList.add("http://www.cool-proxy.net/proxies/http_proxy_list/page:1/sort:score/direction:desc");
		proxySeedList.add("http://204.45.118.186/plists.json.php");
		// proxySeedList.add("http://www.simpleproxylist.com/");
		proxySeedList.add("http://www.mrhinkydink.com/proxies.htm");
		proxySeedList.add("https://nordvpn.com/free-proxy-list/1/?allc=all&allp=all&port&sortby=0&way=1&pp=1");
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String url = (String) task.get("url");
		DefaultHttpClient client = HttpRequestManager.getDefaultManager().getClient();

		BasicClientCookie cookie = new BasicClientCookie("__utma",
				"193324902.1016719687.1401026096.1401026096.1401026096.1");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmc", "193324902");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmz",
				"193324902.1401026096.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
		client.getCookieStore().addCookie(cookie);
		JSONObject jObject = new JSONObject();
		JSONArray nextArray = new JSONArray();
		if (StringUtils.isEmpty(url)) {
			for (String seedUrl : proxySeedList) {
				nextArray.put(seedUrl);
			}
		} else {
			List<String> nextUrls = handleUrl(url, jObject, client);
			for (String nextUrl : nextUrls) {
				nextArray.put(nextUrl);
			}
		}
		JSONUtils.put(jObject, "nexts", nextArray);
		return jObject.toString();
	}

	private List<String> handleUrl(String url, JSONObject jObject, DefaultHttpClient client) throws Exception {
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", url);
		String html = HttpClientUtils.getContent(client, get);
		html = decode(html);
		Pattern oReg = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)[^0-9]+?([0-9]{2,})", Pattern.MULTILINE);
		Matcher matcher = oReg.matcher(html);
		String key = "proxys";
		JSONArray proxyArray = JSONUtils.get(jObject, key);
		if (proxyArray == null) {
			proxyArray = new JSONArray();
			JSONUtils.put(jObject, key, proxyArray);
		}
		while (matcher.find()) {
			JSONObject ipObject = new JSONObject();
			JSONUtils.put(ipObject, "ip", matcher.group(1));
			JSONUtils.put(ipObject, "port", matcher.group(2));
			proxyArray.put(ipObject);
			System.out.println(matcher.group(1) + ":" + matcher.group(2));
		}
		Document dom = Jsoup.parse(html, url);
		Elements nextElements = dom.select(".next a[href]:contains(Next),a[href*=p=]:contains(Next)");
		List<String> nextList = new ArrayList<String>();
		if (!nextElements.isEmpty()) {
			String nextUrl = nextElements.first().absUrl("href");
			nextList.add(nextUrl);
		} else if (url.endsWith("proxies.htm")) {
			nextElements = dom.select("a.menu[href*=proxies]:contains(page)");
			for (int i = 1; i < nextElements.size(); i++) {
				nextList.add(nextElements.get(i).absUrl("href"));
			}
		} else if (url.startsWith("https://nordvpn.com/free-proxy-list/1/")) {
			nextElements = dom.select("#proxy_list div.full_width div.pagination div a[title*=Last][href*=page=]");
			if (!nextElements.isEmpty()) {
				String lastPageUrl = nextElements.first().attr("href");
				Pattern oPageReg = Pattern.compile("(?<=&page\\=)([0-9]+)");
				Matcher pMatcher = oPageReg.matcher(lastPageUrl);
				if (pMatcher.find()) {
					Integer lastPage = Integer.valueOf(pMatcher.group());
					for (int i = 2; i <= lastPage; i++) {
						String nextUrl = String
								.format("https://nordvpn.com/free-proxy-list/%d/?allc=all&allp=all&port&sortby=0&way=1&pp=1",
										i);
						nextList.add(nextUrl);
					}
				}
			}
		}
		return nextList;
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
