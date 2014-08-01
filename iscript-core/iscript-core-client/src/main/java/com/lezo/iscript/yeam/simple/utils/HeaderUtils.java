package com.lezo.iscript.yeam.simple.utils;

import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.client.HardConstant;
import com.lezo.iscript.yeam.client.task.TasksCaller;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.proxy.ProxyBuffer;

public class HeaderUtils {
	public static final String CLIENT_NAME = String.format("%s@%s", ClientPropertiesUtils.getProperty("name"),
			HardConstant.MAC_ADDR);
	private static final long PROXY_REPORT_TIME = 5 * 60 * 1000;

	public static JSONObject getHeader() {
		JSONObject headObject = new JSONObject();
		JSONUtils.put(headObject, "cstamp", ConfigParserBuffer.getInstance().getStamp());
		ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
		JSONUtils.put(headObject, "tactive", caller.getActiveCount());
		JSONUtils.put(headObject, "tmax", caller.getLargestPoolSize());
		JSONUtils.put(headObject, "tsize", caller.getQueue().size());
		JSONUtils.put(headObject, "name", CLIENT_NAME);
		ProxyBuffer proxyBuffer = ProxyBuffer.getInstance();
		if (System.currentTimeMillis() - proxyBuffer.getStamp() > PROXY_REPORT_TIME) {
			JSONUtils.put(headObject, "proxyactive", proxyBuffer.getProxys().size());
			JSONUtils.put(headObject, "proxyerrors", proxyBuffer.getErrors());
			proxyBuffer.setStamp(System.currentTimeMillis());
		}
		return headObject;
	}
}
