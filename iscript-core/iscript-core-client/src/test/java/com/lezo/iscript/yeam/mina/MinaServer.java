package com.lezo.iscript.yeam.mina;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.handler.chain.ChainedIoHandler;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaServer {

	public static void main(String[] args) throws IOException {
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		acceptor.setHandler(new ChainedIoHandler());

		// The logger, if needed. Commented atm
		// DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		// chain.addLast("logger", new LoggingFilter());

		SocketSessionConfig scfg = acceptor.getSessionConfig();

		acceptor.bind(new InetSocketAddress(1081));
		System.out.println("Server started...");
	}
}
