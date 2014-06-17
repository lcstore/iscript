package com.lezo.iscript.yeam.ua;

import org.junit.Test;

public class UABuilder {
	public static UaFactory LOG_UA_FACTORY = new LogUaFactory();
	public static UaFactory LOGIN_UA_FACTORY = new LoginUaFactory();
	public static UaFactory LOAD_UA_FACTORY = new LoadUaFactory();

	public static String newUa(String opt, UaFactory factory) {
		// return factory.createUa(opt);
		return null;
	}

	@Test
	public void testLogUa() throws Exception {
		UaFactory factory = LOG_UA_FACTORY;
		String opt = factory.getUaOpt().toString();
		String ua = UABuilder.newUa(opt, factory);
		System.out.println("#1:" + ua);
	}

	@Test
	public void testLoginUa() throws Exception {
		UaFactory factory = LOGIN_UA_FACTORY;
		String opt = factory.getUaOpt().toString();
		String ua = UABuilder.newUa(opt, factory);
		System.out.println("#1:" + ua);
	}

	@Test
	public void testLoadUa() throws Exception {
		UaFactory factory = LOAD_UA_FACTORY;
		String opt = factory.getUaOpt().toString();
		String ua = UABuilder.newUa(opt, factory);
		System.out.println("#1:" + ua);
	}
}
