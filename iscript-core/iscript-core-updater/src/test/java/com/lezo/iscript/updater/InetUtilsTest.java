package com.lezo.iscript.updater;

import java.net.UnknownHostException;

import org.junit.Test;

import com.lezo.iscript.updater.utils.InetUtils;

public class InetUtilsTest {

	@Test
	public void testWAN() throws UnknownHostException {
		System.err.println("WAN:" + InetUtils.getWANHost());
	}
}
