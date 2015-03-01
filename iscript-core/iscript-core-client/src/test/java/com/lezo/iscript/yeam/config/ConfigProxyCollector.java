package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.encrypt.Base64Decryptor;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxyCollector implements ConfigParser {
	private List<String> proxySeedList;
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

	public ConfigProxyCollector() {
		proxySeedList = new ArrayList<String>();
		proxySeedList.add("http://www.cool-proxy.net/proxies/http_proxy_list/page:1/sort:score/direction:desc");
		proxySeedList.add("http://204.45.118.186/plists.json.php");
		// proxySeedList.add("http://www.simpleproxylist.com/");
		proxySeedList.add("http://www.mrhinkydink.com/proxies.htm");
		proxySeedList.add("https://nordvpn.com/free-proxy-list/1/?allc=all&allp=all&port&sortby=0&way=1&pp=1");
		for (int i = 0; i < 10; i++) {
			proxySeedList
					.add("http://mianfeidaili.ttju.cn/getAgent.php?uCard=%C7%EB%CC%EE%D0%B4%D4%DE%D6%FA%BF%A8%BA%C5%2C%BF%C9%B2%BB%CC%EE%D0%B4&pCard=%C7%EB%CC%EE%D0%B4%D4%DE%D6%FA%BF%A8%C3%DC%2C%BF%C9%B2%BB%CC%EE%D0%B4&Number=%C7%EB%CC%EE%D0%B4%CB%F9%D0%E8%B5%C4%CA%FD%C1%BF&Area=%C7%EB%CC%EE%D0%B4%CB%F9%D0%E8%B5%C4%B5%D8%C7%F8&Operators=%C7%EB%CC%EE%D0%B4%CB%F9%D0%E8%B5%C4%D4%CB%D3%AA%C9%CC&port=%C7%EB%CC%EE%D0%B4%CB%F9%D0%E8%B5%C4%B6%CB%BF%DA&list=Blist");
		}
		proxySeedList.add("http://www.xici.net.co/");
		proxySeedList.add("http://www.samair.ru/proxy/");
		proxySeedList.add("http://www.proxy4free.com/page1.html");
		proxySeedList.add("http://proxy-list.org/");
		proxySeedList.add("http://www.cnproxy.com/proxy1.html");
		proxySeedList.add("http://www.cooleasy.com/");
		proxySeedList.add("http://tools.rosinstrument.com/proxy/");
		proxySeedList.add("http://www.socks-proxy.net/");
		proxySeedList.add("http://free-proxy.cz/en/");
		proxySeedList.add("http://www.cybersyndrome.net/plr.html");
		proxySeedList.add("http://www.cybersyndrome.net/pla.html");
		proxySeedList.add("http://spys.ru/free-proxy-list/CN/");
		proxySeedList.add("http://www.google-proxy.net/");
		proxySeedList.add("http://proxylist.hidemyass.com/");
		proxySeedList.add("http://www.proxynova.com/proxy-server-list/");
		proxySeedList.add("http://checkerproxy.net/all_proxy");
		proxySeedList.add("http://www.cool-proxy.net/proxies/http_proxy_list/sort:score/direction:desc");
		proxySeedList.add("http://www.xroxy.com/proxylist.htm");
		proxySeedList.add("http://spys.ru/en/free-proxy-list/");
		proxySeedList.add("https://nordvpn.com/free-proxy-list/");
		proxySeedList.add("http://sockslist.net/list/proxy-socks-5-list/");
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String url = (String) task.get("url");

		BasicClientCookie cookie = new BasicClientCookie("__utma", "193324902.1016719687.1401026096.1401026096.1401026096.1");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmc", "193324902");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmz", "193324902.1401026096.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
		client.getCookieStore().addCookie(cookie);
		JSONObject jObject = new JSONObject();
		JSONArray nextArray = new JSONArray();
		if (StringUtils.isEmpty(url)) {
			for (String seedUrl : proxySeedList) {
				nextArray.put(seedUrl);
			}
			offerNexts(nextArray);
		} else {
			List<String> nextUrls = handleUrl(url, jObject, client);
			for (String nextUrl : nextUrls) {
				if (!StringUtils.isEmpty(nextUrl)) {
					nextArray.put(nextUrl);
				}
			}
		}
		JSONArray tArray = new JSONArray();
		tArray.put("ProxyAddrDto");
		JSONUtils.put(jObject, "targetList", tArray);

		JSONObject taskObject = new JSONObject();
		JSONUtils.put(taskObject, "nextList", nextArray);

		JSONObject returnObject = new JSONObject();
		JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, taskObject);
		JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, jObject);
		return returnObject.toString();
	}

	private void offerNexts(JSONArray nextArray) throws Exception {
		List<String> homeList = new ArrayList<String>();
		homeList.add("http://www.xunluw.com/IP/");
		homeList.add("http://www.mesk.cn/");
		Set<String> urlSet = new HashSet<String>();
		for (String url : homeList) {
			try {
				while (true) {
					HttpGet get = new HttpGet(url);
					String html = HttpClientUtils.getContent(client, get, "gbk");
					Document dom = Jsoup.parse(html, url);
					Elements urlEls = dom.select("a[href][target]");
					for (Element e : urlEls) {
						String proxyUrl = e.absUrl("href");
						if (proxyUrl.contains("/IP/") || proxyUrl.contains("/ip/")) {
							urlSet.add(proxyUrl);
						}
					}
					Elements nextEls = dom.select("a[href]:containsOwn(下一页)");
					if (nextEls.isEmpty()) {
						break;
					} else {
						String nextUrl = nextEls.first().absUrl("href");
						if (url.equals(nextUrl)) {
							break;
						}
						url = nextUrl;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (String newUrl : urlSet) {
			nextArray.put(newUrl);
		}

	}

	private List<String> handleUrl(String url, JSONObject jObject, DefaultHttpClient client) throws Exception {
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", url);
		String html = HttpClientUtils.getContent(client, get);
		html = decode(html);
		Pattern oReg = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)[^0-9]+?([0-9]{2,})", Pattern.MULTILINE);
		Matcher matcher = oReg.matcher(html);
		String key = "dataList";
		JSONArray proxyArray = JSONUtils.get(jObject, key);
		if (proxyArray == null) {
			proxyArray = new JSONArray();
			JSONUtils.put(jObject, key, proxyArray);
		}
		while (matcher.find()) {
			JSONObject ipObject = new JSONObject();
			String ipString = matcher.group(1);
			JSONUtils.put(ipObject, "ip", InetAddressUtils.inet_aton(ipString));
			JSONUtils.put(ipObject, "port", Integer.valueOf(matcher.group(2)));
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
						String nextUrl = String.format("https://nordvpn.com/free-proxy-list/%d/?allc=all&allp=all&port&sortby=0&way=1&pp=1", i);
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
