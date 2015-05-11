package com.lezo.iscript.yeam.resultmgr.listener;

import java.util.Iterator;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class RetryListener implements IResultListener {
	private static Logger logger = LoggerFactory.getLogger(RetryListener.class);

	@Override
	public void handle(ResultWritable result) {
		if (ResultWritable.RESULT_SUCCESS == result.getStatus()) {
			return;
		}
		JSONObject gObject = JSONUtils.getJSONObject(result.getResult());
		JSONObject argsObject = JSONUtils.get(gObject, "args");
		JSONObject exObject = JSONUtils.get(gObject, "ex");
		logger.warn(String.format("type:%s,taskId:%s,args:%s,cause:%s.", result.getType(), result.getTaskId(),
				argsObject, exObject));
		Integer retry = JSONUtils.getInteger(argsObject, "retry");
		if (retry == null) {
			return;
		} else if (retry >= 3) {
			return;
		}
		TaskWritable tWritable = new TaskWritable();
		tWritable.setId(result.getTaskId());
		Iterator<?> it = argsObject.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			tWritable.put(key, JSONUtils.getObject(argsObject, key));
		}
		tWritable.put("retry", retry + 1);
		logger.warn(String.format("type:%s,taskId:%s,retry:%s...", result.getType(), result.getTaskId(), retry));
//		Integer level = JSONUtils.getInteger(argsObject, "level");
//		level = level == null ? 0 : level;
		TaskCacher.getInstance().getQueue(result.getType()).offer(tWritable, TaskWritable.LEVEL_MAX);
	}
}
