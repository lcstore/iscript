package com.lezo.iscript.utils;

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
	}
}
