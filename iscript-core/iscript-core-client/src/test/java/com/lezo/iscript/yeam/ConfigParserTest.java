package com.lezo.iscript.yeam;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Test;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.Config1688Category;
import com.lezo.iscript.yeam.config.Config1688List;
import com.lezo.iscript.yeam.config.Config1688Product;
import com.lezo.iscript.yeam.config.ConfigEtaoSimilar;
import com.lezo.iscript.yeam.config.ConfigProxyCollector;
import com.lezo.iscript.yeam.config.ConfigProxyDetector;
import com.lezo.iscript.yeam.config.ConfigYhdList;
import com.lezo.iscript.yeam.config.FDSSigner;
import com.lezo.iscript.yeam.config.God360Signer;
import com.lezo.iscript.yeam.config.HuihuiSigner;
import com.lezo.iscript.yeam.config.JDBBSSigner;
import com.lezo.iscript.yeam.config.JDCid2PList;
import com.lezo.iscript.yeam.config.Latest163News;
import com.lezo.iscript.yeam.config.LatestSohuNews;
import com.lezo.iscript.yeam.config.ScriptConfigParser;
import com.lezo.iscript.yeam.config.StringLinker;
import com.lezo.iscript.yeam.config.ZYueSigner;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigParserTest {

	@Test
	public void test() throws Exception {
		ConfigParser parser = new StringLinker();
		TaskWritable task = new TaskWritable();
		task.put("x", "i am lezo");
		task.put("y", "i am lezo");
		try {
			String result = parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testHuihuiSigner() throws Exception {
		ConfigParser parser = new HuihuiSigner();
		TaskWritable task = new TaskWritable();
		task.put("user", "lcs");
		task.put("pwd", "");
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
			long ip = 1567820005;
			task.put("ip", "92.222.153.153");
			task.put("port", 7808);
			task.put("ip", ip);
			task.put("port", 7808);
			// task.put("url", url);
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
		String url = "http://www.yhd.com/ctg/s2/c33827-0//#page=1&sort=2";
		ConfigParser parser = new ConfigYhdList();
		TaskWritable task = new TaskWritable();
		try {
			task.put("url", url);
			task.put("getNexts", 1);
			String result = parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
