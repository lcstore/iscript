package com.lezo.iscript.yeam.client.listener;

import java.util.EventListener;

import com.lezo.iscript.yeam.client.event.CallEvent;

public interface CallEventListener extends EventListener {
	void handleEvent(CallEvent event);
}
