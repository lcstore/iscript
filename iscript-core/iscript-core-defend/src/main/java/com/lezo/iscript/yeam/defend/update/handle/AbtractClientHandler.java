package com.lezo.iscript.yeam.defend.update.handle;

import com.lezo.iscript.yeam.defend.DefendClient;

public abstract class AbtractClientHandler implements ClientHandle {

	@Override
	public void handleClient(DefendClient client) throws Exception {
		doHandle(client);
		if (getNextHandler() != null) {
			getNextHandler().handleClient(client);
		}
	}

	public abstract boolean doHandle(DefendClient client) throws Exception;

	public ClientHandle getNextHandler() {
		return null;
	}

}
