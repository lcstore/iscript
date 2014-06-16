package com.lezo.iscript.yeam.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

public class ClientIoFilter extends IoFilterAdapter {

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		IoBuffer buffer = (IoBuffer) message;
		Object cmdObject = session.getAttribute("cmdName");
		System.out.println("msg:" + this.getClass().getSimpleName() + ",cmdObject:" + cmdObject+",index:"+buffer);
		super.messageReceived(nextFilter, session, message);
	}
}
