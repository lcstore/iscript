package com.lezo.iscript.yeam.client.task;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ResultConstant;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskCallable implements Callable<ResultWritable> {
	private static Logger logger = LoggerFactory.getLogger(TaskCallable.class);
	private TaskWritable task;

	public TaskCallable(TaskWritable task) {
		super();
		this.task = task;
	}

	@Override
	public ResultWritable call() throws Exception {
		ResultWritable rsWritable = new ResultWritable();
		rsWritable.setTaskId(task.getId());
		JSONObject rsObject = new JSONObject();
		rsObject.put("args", new JSONObject(task.getArgs()));
		try {
			String type = task.get("type").toString();
			ConfigParser parser = ConfigParserBuffer.getInstance().getParser(type);
			String rs = parser.doParse(task);
			rsObject.put("rs", rs);
			rsWritable.setStatus(ResultConstant.RESULT_SUCCESS);
		} catch (Exception e) {
			rsWritable.setStatus(ResultConstant.RESULT_FAIL);
			String strStack = ExceptionUtils.getStackTrace(e);
			JSONUtils.put(rsObject, "ex", strStack);
			logger.warn(strStack);
		} finally {
			rsWritable.setResult(rsObject.toString());
		}
		return rsWritable;
	}

}
