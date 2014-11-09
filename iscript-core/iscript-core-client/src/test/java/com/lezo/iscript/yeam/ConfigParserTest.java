package com.lezo.iscript.yeam;

import java.io.File;
import java.io.FileFilter;
import java.net.InetAddress;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.Config1688Category;
import com.lezo.iscript.yeam.config.Config1688List;
import com.lezo.iscript.yeam.config.Config1688Product;
import com.lezo.iscript.yeam.config.Config360Uploader;
import com.lezo.iscript.yeam.config.ConfigClientWake;
import com.lezo.iscript.yeam.config.ConfigEtaoSimilar;
import com.lezo.iscript.yeam.config.ConfigJdProduct;
import com.lezo.iscript.yeam.config.ConfigJdPromotion;
import com.lezo.iscript.yeam.config.ConfigProxyCollector;
import com.lezo.iscript.yeam.config.ConfigProxyDetector;
import com.lezo.iscript.yeam.config.ConfigYhdCategory;
import com.lezo.iscript.yeam.config.ConfigYhdList;
import com.lezo.iscript.yeam.config.ConfigYhdProduct;
import com.lezo.iscript.yeam.config.ConfigYhdPromotList;
import com.lezo.iscript.yeam.config.ConfigYhdPromotion;
import com.lezo.iscript.yeam.config.FDSSigner;
import com.lezo.iscript.yeam.config.God360Signer;
import com.lezo.iscript.yeam.config.ConfigHuihuiSigner;
import com.lezo.iscript.yeam.config.JDBBSSigner;
import com.lezo.iscript.yeam.config.JDCid2PList;
import com.lezo.iscript.yeam.config.Latest163News;
import com.lezo.iscript.yeam.config.LatestSohuNews;
import com.lezo.iscript.yeam.config.ScriptConfigParser;
import com.lezo.iscript.yeam.config.StringLinker;
import com.lezo.iscript.yeam.config.ZYueSigner;
import com.lezo.iscript.yeam.http.HttpClientFactory;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigParserTest {

	@Test
	public void testConfig() throws Exception {
		ConfigParser parser = new StringLinker();
		parser = new ConfigJdPromotion();
		parser = new ConfigJdProduct();
		// parser = new ConfigJdPromotList();
		// parser = new ConfigBaiduDoc();
		// parser = new ConfigBarCodeCollector();
		// parser = new ConfigBaiduDoc();
		// parser = new ConfigBarCodeMatcher();
		// parser = new ConfigYhdProduct();
		parser = new ConfigYhdPromotion();
//		parser = new ConfigYhdProduct();
//		parser = new ConfigYhdPromotList();
		String url = null;
		// url = "http://item.jd.com/1061139232.html";// barCode
		// url = "http://item.jd.com/104616.html";// sell out
		// url =
		// "http://item.yhd.com/item/10323264?tc=2.0.5.10323264.4&tp=4.111243.m1669872.0.7.K|dT4Pn";
		url = "http://www.yhd.com/?tracker_u=4734&website_id=A100202367";
		url = "http://item.yhd.com/item/10323264?tc=2.0.5.10323264.4&tp=4.111243.m1669872.0.7.K|dT4Pn";
//		url = "http://cms.yhd.com/cmsPage/show.do?pageId=54236";
//		url = "http://item.yhd.com/item/lp/369182_8382612_1";
//		url = "http://cms.yhd.com/cmsPage/show.do?pageId=108589";
//		url = "http://cms.yhd.com/cmsPage/show.do?pageId=110003&tp=4.108589.0.0.0.K|yPYA5";
//		url = "http://cms.yhd.com/cmsPage/show.do?pageId=109548&tp=4.110003.m1631357.0.20.K|yeUxf";
//		url = "http://cms.yhd.com/cmsPage/show.do?pageId=109548&tp=4.110003.m1631357.0.20.K|yQWtI";
		// urlList.add(url);
		TaskWritable task = new TaskWritable();
		task.put("barCode", "6900068005020");
		// task.put("barCode", "9787807514398");
		task.put("barCode", "6903148018194");
		task.put("url", url);
		String result = parser.doParse(task);
		System.out.println("result:" + result);
		try {
			// List<String> urlList = getUrlList();
			// for (String sUrl : urlList) {
			// System.out.println("sUrl:" + sUrl);
			// String result = parser.doParse(task);
			// System.out.println("result:" + result);
			// }
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		System.err.println("Date:" + new Date(1414638164360L).toLocaleString());
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
	public void test() throws Exception {
		ConfigParser parser = new StringLinker();
		TaskWritable task = new TaskWritable();
		// task.put("x", "i am lezo");
		// task.put("y", "i am lezo");
		// try {
		// String result = parser.doParse(task);
		// System.out.println(result);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// }
		System.out.println(new Date(0));
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			task.put("url", url);
			String result = parser.doParse(task);
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
		// url = "";
		try {
			task.put("url", url);
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
		ConfigParser parser = new ConfigYhdList();
		TaskWritable task = new TaskWritable();
		// url="http://www.yhd.com/ctg/searchPage/c33827-0/a-s2-v0-p26-price-d0-f0-m1-rt0-pid-mid0-k/?callback=jsonp1407815117323";
		try {
			task.put("url", url);
			task.put("bid", "");
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
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
			String result = parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConfigClientWake() throws Exception {
		ConfigParser parser = new ConfigClientWake();
		TaskWritable task = new TaskWritable();
		try {
			// String result = parser.doParse(task);
			// System.out.println(result);
			Thread.currentThread().join();
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
			String result = parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
