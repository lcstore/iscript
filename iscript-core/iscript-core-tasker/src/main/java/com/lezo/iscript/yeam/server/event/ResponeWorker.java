package com.lezo.iscript.yeam.server.event;

import com.lezo.iscript.yeam.server.event.handler.ClientEventHandler;

public class ResponeWorker implements Runnable {
	private final ClientEvent event;
	public ResponeWorker(ClientEvent event, ClientEventHandler handler) {
		super();
		this.event = event;
		this.handler = handler;
	}


	private final ClientEventHandler handler;


	@Override
	public void run() {
		handler.handle(event);
	}
}
