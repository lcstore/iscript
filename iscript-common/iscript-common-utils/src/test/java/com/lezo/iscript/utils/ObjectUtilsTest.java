package com.lezo.iscript.utils;

import java.util.Date;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class ObjectUtilsTest {

	@Test
	public void testNew() throws Exception {
		Object target = ObjectUtils.newObject(Object.class);
		Assert.assertNotNull(target);
		Integer intValue = ObjectUtils.newObject(Integer.class, 10);
		Assert.assertNotNull(target);
		Assert.assertNotNull(intValue);
		Assert.assertEquals(new Integer(10), intValue);
	}

	@Test
	public void testCopy() throws Exception {
		SampleDto dto = new SampleDto();
		JSONObject jObject = new JSONObject();
		JSONUtils.put(jObject, "name", "lezo");
		JSONUtils.put(jObject, "age", 18);
		JSONUtils.put(jObject, "bMan", true);
		JSONUtils.put(jObject, "isMan", false);
		JSONUtils.put(jObject, "birthDate", 1384941006000L);
		Object target = ObjectUtils.newObject(SampleDto.class);
		ObjectUtils.copyObject(jObject, target);
		System.out.println(dto);
		System.out.println(target);
	}

	@Test
	public void testCopyCast() throws Exception {
		SampleDto dto = new SampleDto();
		JSONObject jObject = new JSONObject();
		JSONUtils.put(jObject, "name", "lezo");
		JSONUtils.put(jObject, "age", 18);
		JSONUtils.put(jObject, "bMan", true);
		JSONUtils.put(jObject, "isMan", false);
		JSONUtils.put(jObject, "height", 170);
		JSONUtils.put(jObject, "birthDate", new Date());
		Object target = ObjectUtils.newObject(SampleDto.class);
		ObjectUtils.copyObject(jObject, target);
		System.out.println(dto);
		System.out.println(target);
	}

	@Test
	public void testCopyField() throws Exception {
		JSONObject jObject = new JSONObject();
		JSONUtils.put(jObject, "name", "lezo");
		JSONUtils.put(jObject, "age", 18);
		JSONUtils.put(jObject, "bMan", true);
		JSONUtils.put(jObject, "isMan", false);
		JSONUtils.put(jObject, "height", 170);
		Object target = ObjectUtils.newObject(SampleDto.class);
		ObjectUtils.copyObject(jObject, target);
		System.out.println(target);
		ObjectUtils.copyField("height", target, 190F);
		System.out.println(target);
		ObjectUtils.copyField("bMan", target, false);
		System.out.println(target);
	}
}
