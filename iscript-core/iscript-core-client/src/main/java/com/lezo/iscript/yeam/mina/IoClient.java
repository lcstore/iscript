package com.lezo.iscript.yeam.mina;

import java.net.InetSocketAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.client.order.ISessionOrder;
import com.lezo.iscript.client.order.ProxySessionOrder;
import com.lezo.iscript.yeam.io.IoOrder;
import com.lezo.iscript.yeam.mina.filter.ConfigIoFilter;
import com.lezo.iscript.yeam.mina.filter.ProxyIoFilter;
import com.lezo.iscript.yeam.mina.filter.TaskIoFilter;
import com.lezo.iscript.yeam.mina.filter.TimeIoFilter;
import com.lezo.iscript.yeam.mina.filter.TokenIoFilter;
import com.lezo.iscript.yeam.mina.utils.ClientPropertiesUtils;

public class IoClient extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(IoClient.class);
	private static final long CONNECT_TIMEOUT = 20000;
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
		this.connector.getFilterChain().addLast(TimeIoFilter.class.getSimpleName(), new TimeIoFilter());
		this.connector.getFilterChain().addLast(ConfigIoFilter.class.getSimpleName(), new ConfigIoFilter());
		this.connector.getFilterChain().addLast(TokenIoFilter.class.getSimpleName(), new TokenIoFilter());
		this.connector.getFilterChain().addLast(ProxyIoFilter.class.getSimpleName(), new ProxyIoFilter());
		this.connector.getFilterChain().addLast(TaskIoFilter.class.getSimpleName(), new TaskIoFilter());
		this.connector.setHandler(this);
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
		long timeoutMillis = 30000;
		for (;;) {
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(host, this.port));
				future.awaitUninterruptibly(timeoutMillis);
				return future.getSession();
			} catch (Exception e) {
				logger.warn("Can not connect to " + host + ":" + port, e);
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

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (!(message instanceof IoOrder)) {
			return;
		}
		ISessionOrder sessionOrder = new ProxySessionOrder();
		IoOrder ioOrder = (IoOrder) message;
		sessionOrder.execute(ioOrder, session);
	}

}
