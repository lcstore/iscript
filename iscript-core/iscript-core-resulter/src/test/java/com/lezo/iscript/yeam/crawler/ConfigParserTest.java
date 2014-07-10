package com.lezo.iscript.yeam.crawler;

import org.junit.Test;

import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigParserTest {

	@Test
	public void testJdBar() throws Exception {
		TaskWritable task = new TaskWritable();
		task.put("barCode", "8934760211005");
//		task.put("barCode", "4719778004771");
		ConfigParser parser = new JdBarCodeSimilar();
		String rs = parser.doParse(task);
		System.out.println(rs);
	}
}
