package com.lezo.iscript.yeam.mina;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class IOSender {
	private IoConnector connector;
	private IoSession session;
	private String hostname;
	private int port;

	private IOSender() {
	}

	private void ensureOpen() {
		if (session != null && session.isConnected()) {
			return;
		}
		doOpen();
	}

	private IoSession doOpen( ) {
		connector = new NioSocketConnector();
		connector.setHandler(new ClientHandler());
		DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
		filterChain.addLast("config", null);
		filterChain.addLast("task", null);

		while (true) {
			try {
				ConnectFuture connFuture = connector.connect(new InetSocketAddress(hostname, port));
				connFuture.awaitUninterruptibly();
				session = connFuture.getSession();
				break;
			} catch (Exception e) {
				System.err.println("Failed to connect.");
				e.printStackTrace();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return session;
	}
}
