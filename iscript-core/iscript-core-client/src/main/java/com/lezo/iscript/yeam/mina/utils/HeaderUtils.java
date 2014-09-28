package com.lezo.iscript.yeam.mina.utils;

import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;

import com.lezo.iscript.common.MacAddress;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.proxy.ProxyBuffer;
import com.lezo.iscript.yeam.task.TasksCaller;

public class HeaderUtils {
	public static final String MAC_ADDR = MacAddress.getMacAddress();
	public static final String CLIENT_NAME = String
			.format("%s@%s", ClientPropertiesUtils.getProperty("name"), MAC_ADDR);

	public static JSONObject getHeader() {
		JSONObject headObject = new JSONObject();
		JSONUtils.put(headObject, "cstamp", ConfigParserBuffer.getInstance().getStamp());
		ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
		JSONUtils.put(headObject, "tactive", caller.getActiveCount());
		JSONUtils.put(headObject, "tmax", caller.getLargestPoolSize());
		JSONUtils.put(headObject, "tsize", caller.getQueue().size());
		JSONUtils.put(headObject, "name", CLIENT_NAME);
		ProxyBuffer proxyBuffer = ProxyBuffer.getInstance();
		JSONUtils.put(headObject, "proxyactive", proxyBuffer.getProxys().size());
		JSONUtils.put(headObject, "proxyerrors", proxyBuffer.getErrors());
		return headObject;
	}
}
