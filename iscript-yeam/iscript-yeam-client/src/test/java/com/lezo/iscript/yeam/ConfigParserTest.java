package com.lezo.iscript.yeam;

import org.junit.Test;

import com.lezo.iscript.yeam.config.HuihuiSigner;
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
		System.setProperty ("jsse.enableSNIExtension", "false");
		ConfigParser parser = new HuihuiSigner();
		TaskWritable task = new TaskWritable();
		task.put("user", "lcstore@126.com");
		task.put("pwd", "126@9Lezo");
		task.put("user", "ajane2009@163.com");
		task.put("pwd", "AJ3251273aj");
		try {
			String result = parser.doParse(task);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
