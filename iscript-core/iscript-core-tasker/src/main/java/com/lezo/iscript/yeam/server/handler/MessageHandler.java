package com.lezo.iscript.yeam.server.handler;

import org.apache.mina.core.session.IoSession;

public interface MessageHandler {

	void handleMessage(IoSession session, Object message);

}
