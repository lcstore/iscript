package com.lezo.iscript.yeam.server;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;

public class HeadCacher {
	private ConcurrentHashMap<String, Object> headMap = new ConcurrentHashMap<String, Object>();
	private static final HeadCacher INSTANCE = new HeadCacher();

	private HeadCacher() {
	}

	private static HeadCacher getInstace() {
		return INSTANCE;
	}

	public boolean putIfVary(Integer type, JSONObject hObject) {
		String clientName = JSONUtils.getString(hObject, "name");
		String key = clientName + "-" + type;
		Object lastObject = headMap.get(key);
		Integer tactive = JSONUtils.getInteger(hObject, "tactive");
		Integer tsize = JSONUtils.getInteger(hObject, "tsize");
		Integer tCount = tactive + tsize;
		if (lastObject != null && tCount > 0 && tCount.equals(lastObject)) {
			return false;
		} else {
			headMap.put(key, tCount);
			return true;
		}
	}
}
