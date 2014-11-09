package com.lezo.iscript.yeam.server.event.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.server.event.ClientEvent;

public class EmptyEventHandler implements ClientEventHandler {
	private static Logger logger = LoggerFactory.getLogger(EmptyEventHandler.class);

	@Override
	public void handle(ClientEvent event) {
	}

}
