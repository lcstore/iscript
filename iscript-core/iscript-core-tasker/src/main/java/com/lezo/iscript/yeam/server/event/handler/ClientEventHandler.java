package com.lezo.iscript.yeam.server.event.handler;

import com.lezo.iscript.yeam.server.event.ClientEvent;

public interface ClientEventHandler {
	void handle(ClientEvent event);
}
