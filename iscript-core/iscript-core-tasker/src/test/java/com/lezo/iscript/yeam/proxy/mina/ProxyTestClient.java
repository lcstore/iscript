/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.lezo.iscript.yeam.proxy.mina;

import java.net.InetSocketAddress;
import java.net.URL;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.proxy.ProxyConnector;
import org.apache.mina.proxy.handlers.ProxyRequest;
import org.apache.mina.proxy.handlers.http.HttpAuthenticationMethods;
import org.apache.mina.proxy.handlers.http.HttpProxyConstants;
import org.apache.mina.proxy.handlers.http.HttpProxyRequest;
import org.apache.mina.proxy.handlers.socks.SocksProxyConstants;
import org.apache.mina.proxy.handlers.socks.SocksProxyRequest;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * ProxyTestClient.java - Base test class for mina proxy
 * 
 * @author Edouard De Oliveira <a
 *         href="mailto:doe_wanted@yahoo.fr">doe_wanted@yahoo.fr</a>
 * @version $Id: $
 */
public class ProxyTestClient {
	public final static String USER = "TED_KODS";

	public final static String PWD = "EDOUARD";

	public final static String DOMAIN = "MYDOMAIN";

	public final static String WORKSTATION = "MYWORKSTATION";

	private final static boolean useHttp1_1 = false;

	static {
		// We need a JCE provider that handles MD4 hashing.
		if (Security.getProvider("BC") == null) {
		}
	}

	public ProxyTestClient(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println(ProxyTestClient.class.getName()
					+ " <proxy-hostname> <proxy-port> <url> <proxy-method> (<proxy-method> is OPTIONNAL)");
			return;
		}

		// Create proxy connector.
		NioSocketConnector socketConnector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);

		ProxyConnector connector = new ProxyConnector(socketConnector);

		// Set connect timeout.
		connector.setConnectTimeoutMillis(5000);

		URL url = new URL(args[2]);
		int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();

		ProxyRequest req = null;

		if (args.length == 4) {
			if ("SOCKS4".equals(args[3])) {
				req = new SocksProxyRequest(SocksProxyConstants.SOCKS_VERSION_4,
						SocksProxyConstants.ESTABLISH_TCPIP_STREAM, new InetSocketAddress(url.getHost(), port), USER);
			} else if ("SOCKS4a".equals(args[3])) {
				req = new SocksProxyRequest(SocksProxyConstants.ESTABLISH_TCPIP_STREAM, url.getHost(), port, USER);
			} else if ("SOCKS5".equals(args[3])) {
				req = new SocksProxyRequest(SocksProxyConstants.SOCKS_VERSION_5,
						SocksProxyConstants.ESTABLISH_TCPIP_STREAM, new InetSocketAddress(url.getHost(), port), USER);
				((SocksProxyRequest) req).setPassword(PWD);
				((SocksProxyRequest) req).setServiceKerberosName(Socks5GSSAPITestServer.SERVICE_NAME);
			} else {
				req = createHttpProxyRequest(args[2]);
			}
		} else {
			req = createHttpProxyRequest(args[2]);
		}

		ProxyIoSession proxyIoSession = new ProxyIoSession(new InetSocketAddress(args[0], Integer.parseInt(args[1])),
				req);

		// Tests modifying authentication order preferences. First algorithm in
		// list available on server
		// will be used for authentication.
		List<HttpAuthenticationMethods> l = new ArrayList<HttpAuthenticationMethods>();
		l.add(HttpAuthenticationMethods.DIGEST);
		l.add(HttpAuthenticationMethods.BASIC);
		proxyIoSession.setPreferedOrder(l);

		connector.setProxyIoSession(proxyIoSession);

		socketConnector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);

		connector.getFilterChain().addLast("logger", new LoggingFilter());

		// This command is sent when using a socks proxy to request a page from
		// the web server.
		String cmd = "GET " + url.toExternalForm() + " HTTP/1.0" + HttpProxyConstants.CRLF + HttpProxyConstants.CRLF;

		connector.setHandler(new ClientSessionHandler(cmd));

		IoSession session;
		for (;;) {
			try {
				ConnectFuture future = connector.connect();
				future.awaitUninterruptibly();
				session = future.getSession();
				break;
			} catch (RuntimeIoException e) {
				System.err.println("Failed to connect. Retrying in 5 secs ...");
				Thread.sleep(5000);
			}
		}

		// Wait until done
		if (session != null) {
			session.getCloseFuture().awaitUninterruptibly();
		}
		connector.dispose();
		System.exit(0);
	}

	private HttpProxyRequest createHttpProxyRequest(String uri) {
		HttpProxyRequest req = new HttpProxyRequest(uri);
		HashMap<String, String> props = new HashMap<String, String>();
		props.put(HttpProxyConstants.USER_PROPERTY, USER);
		props.put(HttpProxyConstants.PWD_PROPERTY, PWD);
		props.put(HttpProxyConstants.DOMAIN_PROPERTY, DOMAIN);
		props.put(HttpProxyConstants.WORKSTATION_PROPERTY, WORKSTATION);

		req.setProperties(props);
		if (useHttp1_1) {
			req.setHttpVersion(HttpProxyConstants.HTTP_1_1);
		}

		return req;
	}

	public static void main(String[] args) throws Exception {
		args = new String[] { "183.207.224.50", "84", "http://1111.ip138.com/ic.asp" };
		new ProxyTestClient(args);
	}
}