package com.lezo.iscript.yeam.simple;

import java.net.InetSocketAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.simple.event.ResponeProceser;
import com.lezo.iscript.yeam.simple.event.ResponeWorkerFactory;
import com.lezo.iscript.yeam.simple.utils.ClientPropertiesUtils;

public class IoClient extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(IoClient.class);
	private static final long CONNECT_TIMEOUT = 20000;
	private ResponeWorkerFactory workerFactory = new ResponeWorkerFactory();
	private String host;
	private int port;
	private NioSocketConnector connector;
	private IoSession session;

	public IoClient() {
		super();
		this.host = ClientPropertiesUtils.getProperty("host");
		String sPort = ClientPropertiesUtils.getProperty("port");
		assertEmpty("host", host);
		assertEmpty("port", sPort);
		this.port = Integer.valueOf(sPort);
		configConnector();
	}

	public IoClient(String host, int port) {
		super();
		this.host = host;
		this.port = port;
		configConnector();
	}

	private void configConnector() {
		this.connector = new NioSocketConnector();
		this.connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
		this.connector.getFilterChain()
				.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		if (logger.isDebugEnabled()) {
			this.connector.getFilterChain().addLast("logger", new LoggingFilter());
		}
		this.connector.setHandler(this);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message == null) {
			return;
		}
		if (message instanceof IoRespone) {
			IoRespone ioRespone = (IoRespone) message;
			ResponeProceser.getInstance().execute(workerFactory.createWorker(ioRespone));
		} else {
			logger.warn("Get unkown message.className:" + message.getClass().getName());
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		super.messageSent(session, message);
	}

	public IoSession getSession() {
		if (session != null && session.isConnected()) {
			return session;
		}
		synchronized (this) {
			if (session != null && session.isConnected()) {
			} else {
				session = reConnect();
			}
		}
		return session;
	}

	private IoSession reConnect() {
		for (;;) {
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(host, this.port));
				future.awaitUninterruptibly();
				return future.getSession();
			} catch (RuntimeIoException e) {
				logger.warn("Can not coonect to " + host + ":" + port);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void assertEmpty(String key, String value) {
		if (StringUtils.isEmpty(value)) {
			throw new IllegalArgumentException("please set property[" + key + "]");
		}
	}
}
