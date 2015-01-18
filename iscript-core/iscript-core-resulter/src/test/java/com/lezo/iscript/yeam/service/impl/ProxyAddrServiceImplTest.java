package com.lezo.iscript.yeam.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.ProxyAddrDao;
import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;
import com.lezo.iscript.service.crawler.service.impl.ProxyAddrServiceImpl;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;

public class ProxyAddrServiceImplTest {

	@Test
	public void testFindProxys() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProxyAddrDao proxyAddrDao = SpringBeanUtils.getBean(ProxyAddrDao.class);
		ProxyAddrServiceImpl brandService = new ProxyAddrServiceImpl();
		brandService.setProxyAddrDao(proxyAddrDao);
		int maxCount = 1;
		for (int i = 1; i <= maxCount; i++) {
			try {
				// String url =
				// String.format("http://www.proxy.com.ru/list_%s.html", i);
				// String url =
				// String.format("http://www.cybersyndrome.net/pla.html");
				// String url =
				// String.format("http://www.proxylist.ro/free-proxy-list-widget.js?size=20");
				// String url =
				// String.format("http://checkerproxy.net/all_proxy");
				// String url =
				// String.format("http://www.xroxy.com/proxylist.php?port=&type=&ssl=&country=&latency=&reliability=&sort=reliability&desc=true&pnum=%d",
				// i);
				// String url =
				// String.format("http://proxy-list.org/english/index.php?p=%d",
				// i);
				// String url =
				// String.format("http://free-proxy.cz/en/proxylist/main/%d",
				// i);
				String url = String.format("http://www.blackhatworld.com/blackhat-seo/proxy-lists/354949-200-scrapebox-passed-http-proxies-freshly-verified-w-screenshot-daily-updates-%d.html", i);
//				String url = String.format("http://socks5proxies.com/index.php?page=%d&action=freeproxy", i);
				// String url =
				// String.format("http://www.samair.ru/proxy/proxy-%s.htm", i <
				// 10 ? "0" + i : i);
				System.err.println("start to parser:" + url);
				byte[] byteArray = Jsoup.connect(url).header("Accept-Encoding", "gzip, deflate").timeout(120000).userAgent("Mozilla").referrer(url).method(Method.GET).execute().bodyAsBytes();
				String source = new String(byteArray, "gbk");
				List<ProxyAddrDto> pageList = findProxy(source);
				if (pageList.size() < 5) {
					Document dom = Jsoup.parse(source);
					source = dom.text();
					pageList = findProxy(source);
					System.err.println("start[text] to save count:" + pageList.size());
				} else {
					System.err.println("start[html] to save count:" + pageList.size());
				}
				brandService.batchSaveProxyAddrs(pageList);
				TimeUnit.SECONDS.sleep(5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Test
	public void testParserProxys() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProxyAddrDao proxyAddrDao = SpringBeanUtils.getBean(ProxyAddrDao.class);
		ProxyAddrServiceImpl brandService = new ProxyAddrServiceImpl();
		brandService.setProxyAddrDao(proxyAddrDao);
		String source = FileUtils.readFileToString(new File("src/test/resources/proxy.txt"), "UTF-8");
		List<ProxyAddrDto> pageList = findProxy(source);
		if (pageList.size() < 5) {
			Document dom = Jsoup.parse(source);
			source = dom.text();
			pageList = findProxy(source);
			System.err.println("start[text] to save count:" + pageList.size());
		} else {
			System.err.println("start[html] to save count:" + pageList.size());
		}
		brandService.batchSaveProxyAddrs(pageList);

	}

	private List<ProxyAddrDto> findProxy(String source) throws JSONException {
		JSONArray proxyArray = doProxyParser(source);
		List<ProxyAddrDto> dtoList = convert2Dto(proxyArray);
		return dtoList;
	}

	private List<ProxyAddrDto> convert2Dto(JSONArray proxyArray) throws JSONException {
		List<ProxyAddrDto> dtoList = new ArrayList<ProxyAddrDto>(proxyArray.length());
		for (int i = 0; i < proxyArray.length(); i++) {
			JSONObject ipObject = proxyArray.getJSONObject(i);
			ProxyAddrDto dto = new ProxyAddrDto();
			dto.setIp(JSONUtils.getLong(ipObject, "ip"));
			dto.setPort(JSONUtils.getInteger(ipObject, "port"));
			dto.setCreateTime(new Date());
			dto.setUpdateTime(dto.getCreateTime());
			if (dto.getIp() == null || dto.getPort() == null) {
				throw new RuntimeException("check ip and port..");
			}
			dtoList.add(dto);
		}
		return dtoList;
	}

	public JSONArray doProxyParser(String source) {
		Pattern oReg = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)[^0-9]+?([0-9]{2,})", Pattern.MULTILINE);
		Matcher matcher = oReg.matcher(source);
		JSONArray proxyArray = new JSONArray();
		while (matcher.find()) {
			JSONObject ipObject = new JSONObject();
			String ipString = matcher.group(1);
			JSONUtils.put(ipObject, "ip", InetAddressUtils.inet_aton(ipString));
			JSONUtils.put(ipObject, "port", Integer.valueOf(matcher.group(2)));
			proxyArray.put(ipObject);
			System.out.println(matcher.group(1) + ":" + matcher.group(2));
		}
		return proxyArray;
	}
}
