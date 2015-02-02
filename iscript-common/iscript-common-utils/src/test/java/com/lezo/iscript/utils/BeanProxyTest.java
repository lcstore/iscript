package com.lezo.iscript.utils;

import org.junit.Test;

import com.lezo.iscript.common.UnifyValueUtils;

public class BeanProxyTest {

	@Test
	public void testBeanProxy() throws Exception {
		TestNameDto nameDto = new TestNameDto();
		nameDto.setName("keep name");
		System.err.println("before:" + nameDto);
		UnifyValueUtils.unifyObject(nameDto);
		System.err.println("after:" + nameDto);
	}

}
