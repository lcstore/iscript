package com.lezo.iscript.yeam.server.event;

import org.apache.mina.core.session.IoSession;

import com.lezo.iscript.yeam.server.event.handler.AbstractEventHandler;
import com.lezo.iscript.yeam.server.event.handler.ClientEventHandler;
import com.lezo.iscript.yeam.server.event.handler.ConfigEventHandler;
import com.lezo.iscript.yeam.server.event.handler.ResultEventHandler;
import com.lezo.iscript.yeam.server.event.handler.TaskLackEventHandler;

public class ClientEventNotifier {

	public static ClientEvent creatEvent(IoSession session, Object message) {
		ClientEvent event = new ClientEvent();
		event.setSession(session);
		event.setMessage(message);
		return event;
	}

	public static void doNotify(IoSession session, Object message) {
		doNotify(creatEvent(session, message));
	}

	public static void doNotify(ClientEvent event) {
		ResponeProceser.getInstance().execute(new ResponeWorker(event, getResponeHandler()));
		RequestProceser.getInstance().execute(new RequestWorker(event, getRequestHandler()));
	}

	private static class RequestHandlerHolder {
		private static ClientEventHandler handler;
		static {
			AbstractEventHandler configEventHandler = new ConfigEventHandler();
//			AbstractEventHandler proxyEventHandler = new ProxyEventHandler();
			AbstractEventHandler taskEventHandler = new TaskLackEventHandler();

			configEventHandler.setNextHandler(taskEventHandler);
//			proxyEventHandler.setNextHandler(taskEventHandler);
			handler = configEventHandler;
		}
	}

	private static ClientEventHandler getRequestHandler() {
		return RequestHandlerHolder.handler;
	}

	private static class ResponeHandlerHolder {
		private static ClientEventHandler handler = new ResultEventHandler();
	}

	private static ClientEventHandler getResponeHandler() {
		return ResponeHandlerHolder.handler;
	}
}
