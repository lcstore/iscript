package com.lezo.iscript.yeam.server.session;

import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

public interface ActionHandler {
	boolean isFilter(JSONObject hObject);

	void callAction(JSONObject hObject, IoSession ioSession);
}
