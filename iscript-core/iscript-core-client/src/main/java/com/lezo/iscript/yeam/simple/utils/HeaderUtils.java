package com.lezo.iscript.yeam.simple.utils;

import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.client.HardConstant;
import com.lezo.iscript.yeam.client.task.TasksCaller;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;

public class HeaderUtils {

	public static JSONObject getHeader() {
		JSONObject headObject = new JSONObject();
		JSONUtils.put(headObject, "cstamp", ConfigParserBuffer.getInstance().getStamp());
		ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
		JSONUtils.put(headObject, "tactive", caller.getActiveCount());
		JSONUtils.put(headObject, "tmax", caller.getLargestPoolSize());
		JSONUtils.put(headObject, "tsize", caller.getQueue().size());
		JSONUtils.put(headObject, "mac", HardConstant.MAC_ADDR);
		JSONUtils.put(headObject, "osid", HardConstant.OS_UUID);
		return headObject;
	}
}
