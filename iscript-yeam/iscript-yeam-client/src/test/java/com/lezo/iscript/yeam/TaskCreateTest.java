package com.lezo.iscript.yeam;

import org.json.JSONObject;
import org.junit.Test;

public class TaskCreateTest {

	@Test
	public void test() throws Exception{
		String destHost = "http://localhost:8080/resultservlet/service";
		JSONObject jObject = new JSONObject();
		jObject.put("id", 1);
//		jObject.put("x", 1);
//		jObject.put("y", 10);
//		jObject.put("x", "i am prefix.");
//		jObject.put("y", "i am suffix.");
		jObject.put("user", "ajane2009@163.com");
		jObject.put("pwd", "AJ3251273aj");
		jObject.put("yeam_resulter_host", destHost);
		System.out.println(jObject);
	}
}
