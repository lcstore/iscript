package com.lezo.iscript.utils;

import java.net.InetSocketAddress;

import org.junit.Test;

import junit.framework.Assert;

public class InetAddressUtilsTest {

	@Test
	public void testIp2int22() {
//		Assert.assertEquals(910739980, InetAddressUtils.ip2int("194.153.113.198"));
		System.out.println(InetAddressUtils.ip2int("194.153.113.198"));
		System.out.println(InetAddressUtils.inet_aton("194.153.113.198"));
	}
	@Test
	public void testIp2int() {
		Assert.assertEquals(910739980, InetAddressUtils.ip2int("54.72.202.12"));
		Assert.assertEquals(3264836038L, InetAddressUtils.inet_aton("194.153.113.198"));
	}

	@Test
	public void testInt2ip() {
//		Assert.assertEquals("54.72.202.12", InetAddressUtils.int2ip(910739980));
		Assert.assertEquals("54.72.202.12", InetAddressUtils.int2ip(86244048));
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
