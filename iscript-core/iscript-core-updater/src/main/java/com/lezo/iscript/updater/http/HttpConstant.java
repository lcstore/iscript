package com.lezo.iscript.updater.http;

public class HttpConstant {
	public static final String DEFAULT_CHARSET = "utf-8";
	public static final int DEFAULT_MIN_EXEC_SIZE_VALUE = 50;
	public static final int DEFAULT_MAX_EXEC_SIZE_VALUE = 1000;
	/**
	 * 最大连接数
	 */
	public final static int MAX_TOTAL_CONNECTIONS = 200;
	/**
	 * 获取连接的最大等待时间
	 */
	public final static int DEFAULT_TIMEOUT = 20000;
	/**
	 * 每个路由最大连接数
	 */
	public final static int MAX_ROUTE_CONNECTIONS = 10;
	/**
	 * 连接超时时间
	 */
	public final static int CONNECT_TIMEOUT = 20000;
	/**
	 * 读取超时时间
	 */
	public final static int READ_TIMEOUT = 20000;
}
