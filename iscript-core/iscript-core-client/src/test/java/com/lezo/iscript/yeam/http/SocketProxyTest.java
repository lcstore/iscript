package com.lezo.iscript.yeam.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class SocketProxyTest {
	static class FakeDnsResolver implements DnsResolver {
		@Override
		public InetAddress[] resolve(String host) throws UnknownHostException {
			// Return some fake DNS record for every request, we won't be using
			// it
			return new InetAddress[] { InetAddress.getByAddress(new byte[] { 1, 1, 1, 1 }) };
		}
	}

	static class MyConnectionSocketFactory implements SchemeSocketFactory {
		@Override
		public Socket createSocket(HttpParams params) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isSecure(Socket sock) throws IllegalArgumentException {
			// TODO Auto-generated method stub
			return false;
		}
	}

}
