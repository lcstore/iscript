package com.lezo.iscript.yeam.mina;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

public class TaskIoFilter extends IoFilterAdapter {

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		System.out.println("msg:" + this.getClass().getSimpleName());
		super.messageReceived(nextFilter, session, message);
	}
}
