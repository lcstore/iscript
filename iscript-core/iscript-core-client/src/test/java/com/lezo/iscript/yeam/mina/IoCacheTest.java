package com.lezo.iscript.yeam.mina;

import java.net.InetSocketAddress;

import org.junit.Test;

public class IoCacheTest {

	@Test
	public void test() {
		String hostname = "localhost";
		int port = 8999;
		InetSocketAddress socketAddress = new InetSocketAddress(hostname, port);
		IoCache ioCache = IoCacheBuilder.newIoCache(socketAddress, IoCacheBuilder.TASK_BUILDER);
		ioCache.getSession();
	}
}
