package com.lezo.iscript.yeam.simple.utils;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.client.HardConstant;
import com.lezo.iscript.yeam.client.task.TasksCaller;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.http.ProxyManager;
import com.lezo.iscript.yeam.http.ProxyTracker;

public class HeaderUtils {

	public static JSONObject getHeader() {
		JSONObject headObject = new JSONObject();
		JSONUtils.put(headObject, "cstamp", ConfigParserBuffer.getInstance().getStamp());
		ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
		JSONUtils.put(headObject, "tactive", caller.getActiveCount());
		JSONUtils.put(headObject, "tmax", caller.getLargestPoolSize());
		JSONUtils.put(headObject, "tsize", caller.getQueue().size());
		JSONUtils.put(headObject, "mac", HardConstant.MAC_ADDR);
		JSONUtils.put(headObject, "name", ClientPropertiesUtils.getProperty("name"));

		JSONObject proxyObject = new JSONObject();
		addProxy(proxyObject);
		JSONUtils.put(headObject, "proxy", proxyObject);
		return headObject;
	}

	private static void addProxy(JSONObject proxyObject) {
		ProxyManager proxyManager = HttpUtils.getDefaultHttpRequestManager().getProxyManager();
		List<ProxyTracker> enables = proxyManager.getEnableTrackers();
		List<ProxyTracker> disables = proxyManager.getDiableTrackers();
		JSONUtils.put(proxyObject, "enableNum", enables.size());
		JSONUtils.put(proxyObject, "disableNum", disables.size());
		JSONArray proxyArray = new JSONArray();
		JSONUtils.put(proxyObject, "trackers", proxyArray);
		for (ProxyTracker tracker : enables) {
			JSONObject pObject = new JSONObject();
			JSONUtils.put(pObject, "id", tracker.getId());
			JSONUtils.put(pObject, "total", tracker.getTotalNum().get());
			proxyArray.put(pObject);
		}
		for (ProxyTracker tracker : disables) {
			JSONObject pObject = new JSONObject();
			JSONUtils.put(pObject, "id", tracker.getId());
			JSONUtils.put(pObject, "total", tracker.getTotalNum().get());
			JSONUtils.put(pObject, "errors", tracker.getErrorArray());
			proxyArray.put(pObject);
		}
	}

}
