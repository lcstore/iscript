package com.lezo.iscript.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

public class JSONUtilsTest {

	@Test
	public void testNull() throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x", null);
		args.put("y", "yy");
		JSONObject jObject = new JSONObject(args);
		Iterator it = jObject.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			System.out.println(key + "=" + JSONUtils.getObject(jObject, key));
		}
		System.out.println("nokey=" + JSONUtils.getObject(jObject, "nokey"));
	}
}
