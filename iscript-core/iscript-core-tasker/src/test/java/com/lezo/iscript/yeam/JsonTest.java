package com.lezo.iscript.yeam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class JsonTest {

	@Test
	public void test() throws JSONException {
		System.out.println(new JSONArray("[1,2,3]"));
	}

	@Test
	public void testEquals() throws JSONException {
		JSONObject o1 = new JSONObject();
		o1.put("1", 1);
		JSONObject o2 = new JSONObject();
		o2.put("1", 1);
		System.err.println(o2.equals(o1));
	}

}
