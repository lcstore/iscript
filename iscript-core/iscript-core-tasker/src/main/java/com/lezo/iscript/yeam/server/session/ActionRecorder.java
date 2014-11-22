package com.lezo.iscript.yeam.server.session;

import java.util.concurrent.ConcurrentHashMap;

public class ActionRecorder {
	private static final ActionRecorder INSTANCE = new ActionRecorder();
	private ConcurrentHashMap<String, ActionRecord> recordMap = new ConcurrentHashMap<String, ActionRecord>();

	public static ActionRecorder getInstance() {
		return INSTANCE;
	}

	public ActionRecord getRecord(String key) {
		ActionRecord pRecord = recordMap.get(key);
		if (pRecord == null) {
			recordMap.putIfAbsent(key, new ActionRecord());
			pRecord = recordMap.get(key);
		}
		return pRecord;
	}
}
