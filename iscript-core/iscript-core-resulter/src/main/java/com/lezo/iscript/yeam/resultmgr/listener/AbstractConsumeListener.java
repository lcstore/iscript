package com.lezo.iscript.yeam.resultmgr.listener;

import com.lezo.iscript.yeam.resultmgr.handler.DataHandler;

public abstract class AbstractConsumeListener implements ConsumeListener {

	@Override
	public void doConsume(String type, String data) {
		if (!isAccept(type, data)) {
			return;
		}
	}

	protected abstract DataHandler getHandler();

	protected boolean isAccept(String type, String data) {
		return true;
	}
}
