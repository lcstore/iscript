package com.lezo.iscript.yeam.mina.utils;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.lezo.iscript.common.MacAddress;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.task.TasksCaller;

public class HeaderUtils {
	public static final String MAC_ADDR = MacAddress.getMacAddress();
	public static String CLIENT_NAME;
	private static JSONObject headObject = new JSONObject();
	static {
		String userName = ClientPropertiesUtils.getProperty("name");
		userName = StringUtils.isBlank(userName) ? System.getProperty("user.name", "unknown") : userName;
		CLIENT_NAME = String.format("%s@%s", userName, MAC_ADDR);
		JSONUtils.put(headObject, "name", CLIENT_NAME);
	}

	public static JSONObject getHeader() {
		JSONUtils.put(headObject, "cstamp", ConfigParserBuffer.getInstance().getStamp());
		ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
		JSONUtils.put(headObject, "tactive", caller.getActiveCount());
		JSONUtils.put(headObject, "tmax", caller.getLargestPoolSize());
		JSONUtils.put(headObject, "tsize", caller.getQueue().size());

		// ProxyBuffer proxyBuffer = ProxyBuffer.getInstance();
		// JSONUtils.put(headObject, "proxyactive",
		// proxyBuffer.getProxys().size());
		// JSONUtils.put(headObject, "proxyerrors", proxyBuffer.getErrors());
		return headObject;
	}
}
