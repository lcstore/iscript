package com.lezo.iscript.yeam.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.lezo.iscript.yeam.server.event.RequestProceser;
import com.lezo.iscript.yeam.server.event.RequestWorker;

public class IoServer extends IoHandlerAdapter {
	private IoAcceptor acceptor;

	public IoServer(int port) throws IOException {
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.setHandler(this);
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(port));
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println("Received:" + message);
		RequestProceser.getInstance().execute(new RequestWorker(session, message));
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
	}

}
