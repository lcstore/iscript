package com.lezo.iscript.yeam.task;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskWorker implements Callable<ResultWritable> {
	private static Logger logger = LoggerFactory.getLogger(TaskWorker.class);
	private TaskWritable task;

	public TaskWorker(TaskWritable task) {
		super();
		this.task = task;
	}

	@Override
	public ResultWritable call() throws Exception {
		long start = System.currentTimeMillis();
		ResultWritable rsWritable = new ResultWritable();
		rsWritable.setTaskId(task.getId());
		JSONObject rsObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		String type = null;
		try {
			Object typeObject = task.get("type");
			if (typeObject == null) {
				throw new IllegalArgumentException("No type for task,id:" + task.getId() + ",args:" + argsObject);
			}
			type = typeObject.toString();
			rsWritable.setType(type);
			// argsObject.remove("type");
			ConfigParser parser = ConfigParserBuffer.getInstance().getParser(type);
			String rs = parser.doParse(task);
			rsObject.put("rs", rs);
			rsWritable.setStatus(ResultWritable.RESULT_SUCCESS);
		} catch (Exception e) {
			rsWritable.setStatus(ResultWritable.RESULT_FAIL);
			String strStack = ExceptionUtils.getStackTrace(e);
			JSONObject exObject = new JSONObject();
			JSONUtils.put(exObject, "name", e.getClass().getName());
			JSONUtils.put(exObject, "stack", ExceptionUtils.getStackTrace(e));
			JSONUtils.put(rsObject, "ex", exObject);
			logger.warn(strStack);
		} finally {
			JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);
			JSONUtils.put(rsObject, "args", argsObject);
			rsWritable.setResult(rsObject.toString());
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("done task:%d,type:%s,status:%d,cost:%d", task.getId(), type,
					rsWritable.getStatus(), cost);
			logger.info(msg);
		}
		return rsWritable;
	}

}
