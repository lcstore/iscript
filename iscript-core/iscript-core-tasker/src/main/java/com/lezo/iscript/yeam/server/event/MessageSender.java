package com.lezo.iscript.yeam.server.event;

import org.apache.mina.core.session.IoSession;

import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;

public class MessageSender implements Runnable {
	private IoRespone ioRespone;
	private IoSession ioSession;


	public MessageSender(IoRespone ioRespone, IoSession ioSession) {
		super();
		this.ioRespone = ioRespone;
		this.ioSession = ioSession;
	}


	@Override
	public void run() {
		
	}

}
