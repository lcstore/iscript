package com.lezo.iscript.utils;

import org.junit.Test;

import junit.framework.Assert;

public class InetAddressUtilsTest {

	@Test
	public void testIp2int() {
		Assert.assertEquals(910739980, InetAddressUtils.ip2int("54.72.202.12"));
	}

	@Test
	public void testInt2ip() {
		Assert.assertEquals("54.72.202.12", InetAddressUtils.int2ip(910739980));
	}
	@Test
	public void testIp2int2() {
		Assert.assertEquals(633034921, InetAddressUtils.ip2int("37.187.88.169"));
	}
	
	@Test
	public void testInt2ip2() {
		Assert.assertEquals("37.187.88.169", InetAddressUtils.int2ip(633034921));
	}
}
