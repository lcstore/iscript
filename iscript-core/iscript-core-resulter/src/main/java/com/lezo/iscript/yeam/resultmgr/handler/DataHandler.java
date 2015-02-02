package com.lezo.iscript.yeam.resultmgr.handler;

import org.json.JSONObject;

public interface DataHandler {
	void handle(String type, JSONObject globalObject);
}
