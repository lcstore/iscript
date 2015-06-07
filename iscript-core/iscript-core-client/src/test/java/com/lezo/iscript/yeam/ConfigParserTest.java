package com.lezo.iscript.yeam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.lezo.iscript.rest.http.HttpClientFactory;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.scope.ScriptableUtils;
import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.Config1688Category;
import com.lezo.iscript.yeam.config.Config1688List;
import com.lezo.iscript.yeam.config.Config1688Product;
import com.lezo.iscript.yeam.config.Config360Uploader;
import com.lezo.iscript.yeam.config.ConfigEtaoSimilar;
import com.lezo.iscript.yeam.config.ConfigHuihuiSigner;
import com.lezo.iscript.yeam.config.ConfigJdBrandShop;
import com.lezo.iscript.yeam.config.ConfigJdList;
import com.lezo.iscript.yeam.config.ConfigJdProduct;
import com.lezo.iscript.yeam.config.ConfigJdPromotList;
import com.lezo.iscript.yeam.config.ConfigProxyChecker;
import com.lezo.iscript.yeam.config.ConfigProxyCollector;
import com.lezo.iscript.yeam.config.ConfigProxyDetector;
import com.lezo.iscript.yeam.config.ConfigProxySeedHandler;
import com.lezo.iscript.yeam.config.ConfigProxySeedHandler.ProxyAddrDto;
import com.lezo.iscript.yeam.config.ConfigTmallBrandShop;
import com.lezo.iscript.yeam.config.ConfigTmallProduct;
import com.lezo.iscript.yeam.config.ConfigYhdBrandList;
import com.lezo.iscript.yeam.config.ConfigYhdCategory;
import com.lezo.iscript.yeam.config.ConfigYhdList;
import com.lezo.iscript.yeam.config.ConfigYhdProduct;
import com.lezo.iscript.yeam.config.FDSSigner;
import com.lezo.iscript.yeam.config.God360Signer;
import com.lezo.iscript.yeam.config.JDBBSSigner;
import com.lezo.iscript.yeam.config.JDCid2PList;
import com.lezo.iscript.yeam.config.Latest163News;
import com.lezo.iscript.yeam.config.LatestSohuNews;
import com.lezo.iscript.yeam.config.ScriptConfigParser;
import com.lezo.iscript.yeam.config.ZYueSigner;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigParserTest {

	@Test
	public void testWord() {
		System.err.println((int) 'Z');
	}

	@Test
	public void testConfig() throws Exception {
		ConfigParser parser = new ConfigJdPromotList();
		// parser = new ConfigTmallList();
		parser = new ConfigTmallProduct();
		parser = new ConfigProxyChecker();
		parser = new ConfigYhdBrandList();
		// parser = new ConfigYhdBrandShop();
		parser = new ConfigJdBrandShop();
		parser = new ConfigProxyDetector();
		parser = new ConfigProxySeedHandler();
		parser = new ConfigTmallBrandShop();
		// parser = new ConfigGoogleChecker();
		parser = new ConfigJdProduct();
//		parser = new ConfigJdList();
		String url = null;
		// url = "http://item.jd.com/1061139232.html";// barCode
		// url = "http://item.jd.com/104616.html";// sell out
		url = "http://item.yhd.com/item/992913";
		url = "http://www.yhd.com/brand/c8644.html";
		url = "http://list.yhd.com/b330";
		url = "http://www.jd.com/pinpai/100081.html?enc=utf-8&vt=3#filter";
		url = "http://www.jd.com/pinpai/109.html?enc=utf-8&vt=3#filter";
		url = "http://www.jd.com/pinpai/52369.html?enc=utf-8&vt=3#filter";
		url = "http://www.jd.com/pinpai/1000.html?enc=utf-8&vt=3#filter";
		url = "http://list.tmall.com/search_product.htm?brand=31840&sort=s&style=w#J_Filter";
		url = "http://list.tmall.com/search_product.htm?brand=29295&sort=s&style=w#J_Filter";
		// url = "http://www.yelp.com/biz/ikes-place-san-francisco";
		url = "http://www.baidu.com/";
		// url = "https://www.baidu.com/";
		// url = "http://www.cnbeta.com/";
		url = "http://list.yhd.com/c5066-0-0/?tc=0.0.16.CatMenu_Site_100000019_78010.86&tp=4.94808.161.3.4.KoZbPEM-11-6d7Av";
		url = "http://www.yhd.com/";
		url = "http://www.youdaili.net/Daili/Socks/3166.html";
		url = "http://list.tmall.com/search_product.htm?brand=31840&sort=s&style=w#J_Filter";
		url = "http://list.jd.com/list.html?cat=9987,653,655";
		url = "http://list.jd.com/list.html?cat=9987%2C653%2C655&page=4&JL=6_0_0";
		url = "http://item.jd.com/919669.html";
		url = "http://item.jd.com/1514796.html";
		// urlList.add(url);
		TaskWritable task = new TaskWritable();
		// task.put("barCode", "6900068005020");
		// task.put("barCode", "9787807514398");
		// task.put("barCode", "6903148018194");
		task.put("brandName", "悠哈");
		task.put("brandCode", "330");
		task.put("url", url);

		// http
		task.put("proxyHost", "60.29.248.142");
		task.put("proxyHost", "122.225.106.35");
		task.put("proxyPort", 80);
		task.put("proxyType", 1);
		// task.put("proxyHost", "183.207.224.51");
		// task.put("proxyPort", 80);
		// task.put("proxyPort", 3128);
		// task.put("proxyPort", 3189);
		// socket
		// task.put("ip", "103.246.161.194");
		// task.put("port", 1080);
		// task.put("proxyType", 2);
		//
		// task.put("ip", "222.87.129.218");
		// task.put("port", 83);
		// task.put("proxyType", 1);
		//
		// task.put("url", "http://www.proxy.com.ru/list_%s.html");
		// task.put("seedId", "1");
		// task.put("CreateUrlsFun",
		// "var oUrlArr = [];var maxCount=50;for(var i=1;i<=maxCount;i++){oUrlArr.push(java.lang.String.format(args.url,''+i));}return JSON.stringify(oUrlArr);");
		task.getArgs().remove("proxyHost");
		String returnObject = parser.doParse(task);
		System.out.println("result:" + returnObject);
	}

	@Test
	public void parserUrls() throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		HttpGet get = new HttpGet("http://www.legendsec.org/google.html");
		String html = HttpClientUtils.getContent(client, get, "gbk");
		Document dom = Jsoup.parse(html, get.getURI().toURL().toString());
		Elements destEls = dom.select("tr td a[href*=.][target=_blank]");
		for (Element ele : destEls) {
			System.err.println("urlSet.add(\"" + ele.absUrl("href") + "\");");
		}
	}

	@Test
	public void parserDataList() throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		String sUrl = "http://list.tmall.com/search_product.htm?spm=a220m.1000858.0.0.XJTRBJ&cat=50105688&sort=s&style=g&search_condition=7&from=sn_1_rightnav&active=1&industryCatId=50105688#J_crumbs";
		sUrl = "http://list.tmall.com/search_product.htm?spm=a220m.1000858.0.0.SDnbZM&cat=50025174&sort=s&style=g&search_condition=71&from=sn_1_rightnav&active=1&promo=43906&shopType=any&industryCatId=50026245#J_crumbs";
		String brandUrl = "http://list.tmall.com/ajax/allBrandShowForGaiBan.htm?cat=50020909&sort=s&style=g&search_condition=7&from=sn_1_rightnav&active=1&industryCatId=50023743&spm=a220m.1000858.0.0.yl6e8T&userIDNum=&tracknick=";
		int fromIndex = sUrl.indexOf("?");
		fromIndex = fromIndex < 0 ? 0 : fromIndex;
		int toIndex = sUrl.indexOf("#");
		toIndex = toIndex < 0 ? sUrl.length() : toIndex;
		brandUrl = "http://list.tmall.com/ajax/allBrandShowForGaiBan.htm?" + sUrl.substring(fromIndex + 1, toIndex);
		System.err.println("brandUrl:" + brandUrl);
		HttpGet get = new HttpGet(brandUrl);
		get.addHeader("Refer", sUrl);
		get.addHeader(
				"Cookie",
				"cna=6OyPDT2Pp24CAWXnyHCgg26b; isg=1F8ACFEA6D267706DA5E969E83DE4750; pnm_cku822=138UW5TcyMNYQwiAiwTR3tCf0J%2FQnhEcUpkMmQ%3D%7CUm5Ockt0TnpAeU1xSHJOey0%3D%7CU2xMHDJ%2BH2QJZwBxX39Rb1R6WnQzWjEfSR8%3D%7CVGhXd1llXGNZbVduWmZfZVlsW2ZEeEJ%2BR35Lckt1THBKfkp2S3JcCg%3D%3D%7CVWldfS0RMQQ%2FBCQYJgYoA1kLUSxKJ1Q1UTpZclwKXA%3D%3D%7CVmhIGCQZJgY%2FBycbJBEsDDQOMA4uEi0YJQUwDTAQLBMmGzsBPQdRBw%3D%3D%7CV25Tbk5zU2xMcEl1VWtTaUlwJg%3D%3D; cq=ccp%3D1; CNZZDATA1000279581=699445961-1426908583-http%253A%252F%252Fnvzhuang.tmall.com%252F%7C1427970267; t=5750a246b58b32ac9861e2ee9318193f; uc3=nk2=&id2=&lg2=; tracknick=; ck1=; _tb_token_=c177Nwq7hAjv; cookie2=3bbb2c643f2db222cb3e47f1a629d830; l=AeJ6aKbw4l5i3rdDUB2giWLewlFi3uJe; res=scroll%3A1280*5733-client%3A1280*630-offset%3A1280*5733-screen%3A1280*800; tt=food.tmall.com");

		String html = HttpClientUtils.getContent(client, get, "gbk");
		Pattern oReg = Pattern.compile("brand=([0-9]+)");
		Matcher matcher = oReg.matcher(html);
		Set<String> brandSet = new HashSet<String>();
		while (matcher.find()) {
			brandSet.add(matcher.group(1));
		}
		System.err.println("brand.count:" + brandSet.size());
		Writer out = new FileWriter(new File("src/test/resources/data/tm.brandId.txt"), true);
		BufferedWriter bw = new BufferedWriter(out);
		for (String brand : brandSet) {
			bw.append(brand);
			bw.append("\n");
		}
		IOUtils.closeQuietly(bw);
	}

	@Test
	public void parserGoogleUrls() throws Exception {
		final DefaultHttpClient client = HttpClientUtils.createHttpClient();
		HttpGet get = new HttpGet("http://www.legendsec.org/google.html");
		String html = HttpClientUtils.getContent(client, get, "gbk");
		Document dom = Jsoup.parse(html, get.getURI().toURL().toString());
		Elements destEls = dom.select("tr td a[href*=.][target=_blank]");
		final Map<String, Long> urlCostMap = new HashMap<String, Long>();
		ExecutorService exec = Executors.newFixedThreadPool(10);
		for (final Element ele : destEls) {
			exec.execute(new Runnable() {
				@Override
				public void run() {
					try {
						long startMills = System.currentTimeMillis();
						String url = ele.absUrl("href");
						HttpGet get = new HttpGet(url);
						HttpResponse respone = client.execute(get);
						String html = EntityUtils.toString(respone.getEntity(), "UTF-8");
						if (html.indexOf("searchform") > 0) {
							urlCostMap.put(url, System.currentTimeMillis() - startMills);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		exec.shutdown();
		while (!exec.isTerminated()) {
			System.err.println("wait a mills");
			TimeUnit.SECONDS.sleep(60);
		}
		List<Map.Entry<String, Long>> entryList = new ArrayList<Map.Entry<String, Long>>(urlCostMap.entrySet());
		java.util.Collections.sort(entryList, new Comparator<Map.Entry<String, Long>>() {
			@Override
			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		FileUtils.writeLines(new File("src/test/resources/google-ip.txt"), "UTF-8", entryList);
		for (Entry<String, Long> entry : entryList) {
			System.err.println(entry.getKey() + " : " + entry.getValue());
		}
	}

	private List<String> getUrlList() throws Exception {
		List<String> urlList = new ArrayList<String>();
		String url = "http://wenku.baidu.com/link?url=Si4IfoMJH7ogXfzpxbadVUzKRE5c6gyxgDmbR9nMwbE82bNhWqLX8YC7yhbiMOHn7ASnM-cD_PnDm-PqpdSTfPZdpoer6aTmP7LEyzWOOcS";
		urlList.add(url);
		// for (int i = 2; i <= 351; i++) {
		// urlList.add(url + "?page=" + i);
		// }
		return urlList;
	}

	@Test
	public void testBarMerge() throws Exception {
		File parent = new File("D:/codes/lezo/barcode.src");
		File[] fileArr = parent.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".log");
			}
		});
		JSONArray dArray = new JSONArray();
		int total = 0;
		Set<String> hasCodeSet = new HashSet<String>();
		for (int i = 0; i < fileArr.length; i++) {
			File file = fileArr[i];
			String source = FileUtils.readFileToString(file, "UTF-8");
			Pattern oReg = Pattern.compile("[0-9]{13}");
			Matcher matcher = oReg.matcher(source);
			while (matcher.find()) {
				String sBarCode = matcher.group();
				if (BarCodeUtils.isBarCode(sBarCode)) {
					if (!hasCodeSet.contains(sBarCode)) {
						dArray.put(sBarCode);
						hasCodeSet.add(sBarCode);
						if (dArray.length() >= 1000) {
							System.out.println(dArray);
							total += dArray.length();
							dArray = new JSONArray();
						}
					}
				}
			}
		}
		total += dArray.length();
		System.out.println(dArray);
		System.out.println("total:" + total);
	}

	@Test
	public void testProxySeedHandler() throws Exception {
		// 185.46.151.122 3128
		ConfigProxySeedHandler parser = new ConfigProxySeedHandler();
		String source = FileUtils.readFileToString(new File("src/test/resources/data/proxylist.hidemyass.com.har"),
				"UTF-8");
		String decodePageFun = " var src =args.source;  var oData = eval('(' + src + ')'); src = oData.log.entries[0].response.content.text; oData = eval('(' + src + ')');";
		decodePageFun = FileUtils.readFileToString(new File("src/test/resources/proxydecoder.js"), "UTF-8");
		// source = parser.doDecode(source, decodePageFun);
		List<ProxyAddrDto> proxyList = parser.findProxy(source, decodePageFun);

		System.err.println("proxyList:" + proxyList.size());
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, proxyList);
		String dataString = writer.toString();
		System.err.println("dataString:" + dataString);
	}

	@Test
	public void testProxyWeb() throws Exception {
		String source = FileUtils.readFileToString(new File("src/test/resources/prxys.com.urlbuilder.js"), "UTF-8");
		Context cx = Context.enter();
		Scriptable parent = ScriptableUtils.getJSONScriptable();
		Scriptable newScope = cx.newObject(parent);
		newScope.setPrototype(parent);
		newScope.setParentScope(null);
		Object rsObject = cx.evaluateString(newScope, source, "decode", 0, null);
		String src = Context.toString(rsObject);
		java.lang.System.err.println(src);
	}

	@Test
	public void testHuihuiSigner() throws Exception {
		ConfigParser parser = new ConfigHuihuiSigner();
		TaskWritable task = new TaskWritable();
		task.put("user", "dlinked@126.com");
		task.put("pwd", "dl1234");
		task.put("user", "pis1002@163.com");
		task.put("pwd", "pis1234");
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNullArgs() throws Exception {
		String config = FileUtils.readFileToString(new File("src/test/java/com/lezo/iscript/yeam/config/sum.xml"));
		ConfigParser parser = new ScriptConfigParser(config);
		TaskWritable task = new TaskWritable();
		task.put("type", "sum");
		task.put("x", null);
		task.put("y", "i am lezo");
		try {
			String result = (String) parser.doParse(task);
			JSONObject jObject = new JSONObject(result);
			jObject = JSONUtils.get(jObject, "args");
			System.out.println(result);
			System.out.println(jObject);
			String x = JSONUtils.get(jObject, "x");
			System.out.println("x:" + x);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testJDC2list() throws Exception {
		ConfigParser parser = new JDCid2PList();
		TaskWritable task = new TaskWritable();
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFDSSigner() throws Exception {
		ConfigParser parser = new FDSSigner();
		TaskWritable task = new TaskWritable();
		task.put("user", "lc");
		task.put("pwd", "fd");
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test360Signer() throws Exception {
		ConfigParser parser = new God360Signer();
		// TODO:
		TaskWritable task = new TaskWritable();
		task.put("user", "l");
		task.put("pwd", "3");
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJDBBSSigner() throws Exception {
		ConfigParser parser = new JDBBSSigner();
		TaskWritable task = new TaskWritable();
		task.put("user", "");
		task.put("pwd", "");
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testZYueSigner() throws Exception {
		ConfigParser parser = new ZYueSigner();
		TaskWritable task = new TaskWritable();
		task.put("user", "");
		task.put("pwd", "");
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test360News() throws Exception {
		ConfigParser parser = new Latest163News();
		TaskWritable task = new TaskWritable();
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSohuNews() throws Exception {
		ConfigParser parser = new LatestSohuNews();
		TaskWritable task = new TaskWritable();
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConfig1688Category() throws Exception {
		String url = "http://s.1688.com/selloffer/offer_search.htm?spm=a260k.635.794254077.10&keywords=%BF%AA%D0%C4%B9%FB&descendOrder=true&from=industrySearch&industryFlag=food&sortType=booked&uniqfield=userid&n=y&filt=y";
		ConfigParser parser = new Config1688Category();
		TaskWritable task = new TaskWritable();
		try {
			task.put("url", url);
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConfig1688List() throws Exception {
		String url = "http://s.1688.com/selloffer/offer_search.htm?spm=a260k.635.794254077.10&keywords=%BF%AA%D0%C4%B9%FB&descendOrder=true&from=industrySearch&industryFlag=food&sortType=booked&uniqfield=userid&n=y&filt=y";
		ConfigParser parser = new Config1688List();
		TaskWritable task = new TaskWritable();
		try {
			task.put("url", url);
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConfig1688Product() throws Exception {
		String url = "http://detail.1688.com/offer/1225054841.html";
		url = "http://detail.1688.com/offer/37687586366.html";
		url = "http://detail.1688.com/offer/37687586366.html";
		ConfigParser parser = new Config1688Product();
		TaskWritable task = new TaskWritable();
		try {
			task.put("url", url);
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConfigProxyDetector() throws Exception {
		String url = "http://detail.1688.com/offer/1225054841.html";
		ConfigParser parser = new ConfigProxyDetector();
		TaskWritable task = new TaskWritable();
		try {
			url = "http://www.baidu.com/index.php?tn=19045005_6_pg";
			url = "http://item.jd.com/856850.html";
			long ip = 1567820005;
			task.put("ip", "121.12.255.214");
			task.put("port", 8086);
			task.put("ip", "77.89.244.62");
			task.put("port", 80);
			task.put("url", url);
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConfigProxyCollector() throws Exception {
		String url = "http://www.xroxy.com/proxylist.php?port=&type=&ssl=&country=&latency=&reliability=&sort=reliability&desc=true&pnum=0#table";
		ConfigParser parser = new ConfigProxyCollector();
		TaskWritable task = new TaskWritable();
		url = "https://nordvpn.com/free-proxy-list/1/?allc=all&allp=all&port&sortby=0&way=1&pp=1";
		url = "https://nordvpn.com/free-proxy-list/34/?allc=all&allp=all&port&sortby=0&way=1&pp=1";
		url = "http://mianfeidaili.ttju.cn/getAgent.php?uCard=%C7%EB%CC%EE%D0%B4%D4%DE%D6%FA%BF%A8%BA%C5%2C%BF%C9%B2%BB%CC%EE%D0%B4&pCard=%C7%EB%CC%EE%D0%B4%D4%DE%D6%FA%BF%A8%C3%DC%2C%BF%C9%B2%BB%CC%EE%D0%B4&Number=%C7%EB%CC%EE%D0%B4%CB%F9%D0%E8%B5%C4%CA%FD%C1%BF&Area=%C7%EB%CC%EE%D0%B4%CB%F9%D0%E8%B5%C4%B5%D8%C7%F8&Operators=%C7%EB%CC%EE%D0%B4%CB%F9%D0%E8%B5%C4%D4%CB%D3%AA%C9%CC&port=%C7%EB%CC%EE%D0%B4%CB%F9%D0%E8%B5%C4%B6%CB%BF%DA&list=Blist";
		url = "http://www.xunluw.com/IP/2014/0923/1383.html";
		url = "http://www.mesk.cn/ip/hongkong/2014/0901/1174.html";
		url = "http://www.xunluw.com/IP/2014/0726/1074.html";
		url = "http://www.samair.ru/proxy/";
		url = "http://www.proxy.com.ru/gaoni/";
		// url = "";
		try {
			task.put("url", url);
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConfigEtaoSimilar() throws Exception {
		String url = "http://detail.1688.com/offer/1225054841.html";
		ConfigParser parser = new ConfigEtaoSimilar();
		TaskWritable task = new TaskWritable();
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConfigYhdList() throws Exception {
		String url = "http://www.yhd.com/ctg/s2/c33827-0";
		url = "http://search.yhd.com/s2/c0-0/k%25E6%259D%25BE%25E5%25A1%2594/5/";
		url = "http://www.yhd.com/ctg/s2/vc1730/b/a-s2-v0-p25-price-d0-f0-m1-rt0-pid-mid0-k/?callback=jsonp1407857568774";
		url = "http://www.yhd.com/ctg/s2/c22882-0/";
		url = "http://www.yhd.com/ctg/searchPage/c22882-0/b/a-s2-v0-p17-price-d0-f0-m1-rt0-pid-mid0-k?callback=jsonp1407939421186";
		url = "http://www.yhd.com/ctg/s2/c34032-0-59402/b/a-s2-v0-p1-price-d0-f0-m1-rt0-pid-mid0-k/?tc=3.0.9.59402.3&tp=52.34032.100.0.3.UBdUN8";
		url = "http://list.yhd.com/c22882-0-59582/b/a-s1-v0-p1-price-d0-f0-m1-rt0-pid-mid0-k/?tc=0.0.16.CatMenu_Site_100000003_9024_13360.3&tp=1.0.158.0.2.Kc9q8F5#page=1&sort=2";
		ConfigParser parser = new ConfigYhdList();
		TaskWritable task = new TaskWritable();
		// url="http://www.yhd.com/ctg/searchPage/c33827-0/a-s2-v0-p26-price-d0-f0-m1-rt0-pid-mid0-k/?callback=jsonp1407815117323";
		try {
			task.put("url", url);
			task.put("bid", "");
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConfigYhdProduct() throws Exception {
		String url = "http://item.yhd.com/item/6534749";
		ConfigParser parser = new ConfigYhdProduct();
		TaskWritable task = new TaskWritable();
		try {
			url = "http://item.yhd.com/item/31930307";
			url = "http://item.yhd.com/item/31930307";
			url = "http://item.yhd.com/item/12656572";
			url = "http://item.yhd.com/item/2099463";
			url = "http://item.yhd.com/item/4609570";
			task.put("url", url);
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConfigYhdCategory() throws Exception {
		ConfigParser parser = new ConfigYhdCategory();
		TaskWritable task = new TaskWritable();
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDnsCachePolicy() throws Exception {
		DefaultHttpClient client = HttpClientFactory.createHttpClient();
		for (InetAddress ia : InetAddress.getAllByName("www.baidu.com")) {
			System.err.println(ia);
		}
		InetAddress addr1 = InetAddress.getByName("www.baidu.com");
		System.out.println(addr1.getHostAddress());
		// 在下一行设置断点.
		int i = 0;
		InetAddress addr2 = InetAddress.getByName("www.baidu.com");
		System.out.println(addr2.getHostAddress());
	}

	@Test
	public void testConfig360Uploader() throws Exception {
		ConfigParser parser = new Config360Uploader();
		TaskWritable task = new TaskWritable();
		try {
			String result = (String) parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
