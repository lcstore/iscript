package com.lezo.iscript.yeam;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Test;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.FDSSigner;
import com.lezo.iscript.yeam.config.God360Signer;
import com.lezo.iscript.yeam.config.HuihuiSigner;
import com.lezo.iscript.yeam.config.JDBBSSigner;
import com.lezo.iscript.yeam.config.JDCid2PList;
import com.lezo.iscript.yeam.config.ScriptConfigParser;
import com.lezo.iscript.yeam.config.StringLinker;
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
		System.setProperty("jsse.enableSNIExtension", "false");
		ConfigParser parser = new HuihuiSigner();
		TaskWritable task = new TaskWritable();
		task.put("user", "lcstore@126.com");
		task.put("pwd", "1@6@8Lezo");
		// task.put("user", "ajane2009@163.com");
		// task.put("pwd", "AJ3251273aj");
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
			jObject = JSONUtils.get(jObject, "args", JSONObject.class);
			System.out.println(result);
			System.out.println(jObject);
			String x = JSONUtils.get(jObject, "x", String.class);
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
		task.put("user", "lcstore@126.com");
		task.put("pwd", "fd@9Lezo");
		// task.put("user", "ajane90");
		// task.put("pwd", "fdsAJ90aj");
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
		task.put("user", "lcstore");
		task.put("pwd", "360@9Lezo");
		// task.put("user", "ajane90");
		// task.put("pwd", "fdsAJ90aj");
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
		task.put("user", "lcstore@126.com");
		task.put("pwd", "jd@9Lezo");
		// task.put("user", "ajane90");
		// task.put("pwd", "fdsAJ90aj");
		String url = "http://mercury.jd.com/log.gif?t=www.100000&m=UA-J2011-1&pin=&sid=730406034|1&v=je%3D1%24sc%3D32-bit%24sr%3D1366x768%24ul%3D-%24cs%3D-%24dt%3D%E7%99%BB%E5%BD%95%E4%BA%AC%E4%B8%9C%24hn%3Dpassport.jd.com%24fl%3D-%24os%3Dwin%24br%3Dchrome%24bv%3D35.0.1916.114%24wb%3D1400996865%24xb%3D1400996865%24yb%3D1400996865%24zb%3D1%24cb%3D1%24usc%3Ddirect%24ucp%3D-%24umd%3Dnone%24uct%3D-%24lt%3D0%24ct%3D1400996864707%24tad%3D-&ref=&rm=0.3978388501193162";
		System.out.println(url.substring(74));
		try {
			String result = parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
