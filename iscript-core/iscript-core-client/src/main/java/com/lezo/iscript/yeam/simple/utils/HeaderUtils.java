package com.lezo.iscript.yeam.simple.utils;

import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.client.HardConstant;
import com.lezo.iscript.yeam.client.task.TasksCaller;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.proxy.ProxyBuffer;

public class HeaderUtils {

	private static final long PROXY_REPORT_TIME = 5 * 60 * 1000;

	public static JSONObject getHeader() {
		JSONObject headObject = new JSONObject();
		JSONUtils.put(headObject, "cstamp", ConfigParserBuffer.getInstance().getStamp());
		ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
		JSONUtils.put(headObject, "tactive", caller.getActiveCount());
		JSONUtils.put(headObject, "tmax", caller.getLargestPoolSize());
		JSONUtils.put(headObject, "tsize", caller.getQueue().size());
		JSONUtils.put(headObject, "mac", HardConstant.MAC_ADDR);
		JSONUtils.put(headObject, "name", ClientPropertiesUtils.getProperty("name"));
		ProxyBuffer proxyBuffer = ProxyBuffer.getInstance();
		JSONUtils.put(headObject, "proxyactive", proxyBuffer.getProxys().size());
		if (System.currentTimeMillis() - proxyBuffer.getStamp() > PROXY_REPORT_TIME) {
			JSONUtils.put(headObject, "proxyerrors", proxyBuffer.getErrors());
			proxyBuffer.setStamp(System.currentTimeMillis());
		}
		return headObject;
	}
}
