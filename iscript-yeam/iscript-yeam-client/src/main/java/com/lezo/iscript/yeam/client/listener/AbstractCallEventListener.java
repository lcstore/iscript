package com.lezo.iscript.yeam.client.listener;

import com.lezo.iscript.yeam.client.event.CallEvent;

public abstract class AbstractCallEventListener implements CallEventListener {
	@Override
	public void handleEvent(CallEvent event) {
		if (isAccept(event)) {
			doCallEvent(event);
		}
	}

	protected boolean isAccept(CallEvent event) {
		if (event == null) {
			return false;
		}
		if (event.getType() != getType()) {
			return false;
		}
		return true;
	}

	protected abstract int getType();

	protected abstract void doCallEvent(CallEvent event);

}
