package com.lezo.iscript.yeam.server.event;

import org.apache.mina.core.session.IoSession;

import com.lezo.iscript.yeam.io.IoRequest;

public class DataAnalyzer implements Runnable {
	private IoRequest ioRequest;
	private IoSession ioSession;

	public DataAnalyzer(IoRequest ioRequest, IoSession ioSession) {
		super();
		this.ioRequest = ioRequest;
		this.ioSession = ioSession;
	}

	@Override
	public void run() {

	}

}
