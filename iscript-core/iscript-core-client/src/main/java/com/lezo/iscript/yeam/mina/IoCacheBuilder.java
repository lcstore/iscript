package com.lezo.iscript.yeam.mina;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.AbstractIoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class IoCacheBuilder {
	public static final ConnectBuilder TASK_BUILDER = new ConnectBuilder() {
		@Override
		public AbstractIoConnector newConnector() {
			NioSocketConnector connector = new NioSocketConnector();
			connector.setHandler(this);
			addFilter(connector, new ClientIoFilter());
			addFilter(connector, new ConfigIoFilter());
			addFilter(connector, new TaskIoFilter());
			return connector;
		}
	};

	public static IoCache newIoCache(InetSocketAddress socketAddress, ConnectBuilder buider) {
		IoCache ioCache = new IoCache();
		ioCache.setSocketAddress(socketAddress);
		AbstractIoConnector connector = buider.newConnector();
		ConnectFuture connFuture = connector.connect(ioCache.getSocketAddress());
		connFuture.awaitUninterruptibly();
		IoSession session = connFuture.getSession();
		ioCache.setSession(session);
		ioCache.setConnector(connector);
		return ioCache;
	}
}
