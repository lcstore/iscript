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

	@Test
	public void testCopy() throws Exception {
		SampleDto dto = new SampleDto();
		JSONObject jObject = new JSONObject();
		JSONUtils.put(jObject, "name", "lezo");
		JSONUtils.put(jObject, "age", 18);
		JSONUtils.put(jObject, "bMan", true);
		JSONUtils.put(jObject, "isMan", false);
		Object target = ObjectUtils.newObject(SampleDto.class);
		ObjectUtils.copyObject(jObject, target);
		System.out.println(dto);
		System.out.println(target);
	}
}
