package com.lezo.iscript.yeam.simple;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class IoClient extends IoHandlerAdapter {
	private static final long CONNECT_TIMEOUT = 20000;
	private String host;
	private int port;

	public IoClient(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public void start() {
		NioSocketConnector connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);

		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.setHandler(this);
		IoSession session;

		for (;;) {
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(host, this.port));
				future.awaitUninterruptibly();
				session = future.getSession();
				break;
			} catch (RuntimeIoException e) {
				System.err.println("Failed to connect.");
				e.printStackTrace();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		int i = 0;
		while (++i <= 10) {
			session.write("Send to server." + i);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// wait until the summation is done
//		session.getCloseFuture().awaitUninterruptibly();
//		connector.dispose();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println("Get from server:" + message);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		super.messageSent(session, message);
	}
}
