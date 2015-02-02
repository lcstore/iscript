package com.lezo.iscript.yeam.http;

import java.net.Proxy;
import java.net.Socket;

import org.apache.http.params.HttpParams;

public class ProxySocketFactory extends org.apache.http.conn.scheme.PlainSocketFactory {
	public static final String SOCKET_PROXY = "socket.proxy";

	@Override
	public Socket createSocket(HttpParams params) {
		Object paramObject = params.getParameter(SOCKET_PROXY);
		if (paramObject != null && paramObject instanceof Proxy) {
			Proxy proxy = (Proxy) paramObject;
			return new Socket(proxy);
		}
		return super.createSocket(params);
	}
}
