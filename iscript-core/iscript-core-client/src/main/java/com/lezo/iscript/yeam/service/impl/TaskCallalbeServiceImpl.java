package com.lezo.iscript.yeam.service.impl;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.TaskCallalbeService;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskCallalbeServiceImpl implements TaskCallalbeService {
	private static Logger log = Logger.getLogger(TaskCallalbeServiceImpl.class);

	@Override
	public ResultWritable doCall(TaskWritable task) {
		ResultWritable rsWritable = new ResultWritable();
		JSONObject rsObject = new JSONObject();
		try {
			rsWritable.setTaskId(task.getId());
			String type = task.get("type").toString();
			ConfigParser parser = ConfigParserBuffer.getInstance().getParser(type);
			String rs = parser.doParse(task);
			rsObject.put("rs", rs);
			rsWritable.setStatus(ResultWritable.RESULT_SUCCESS);
		} catch (Exception e) {
			rsWritable.setStatus(ResultWritable.RESULT_FAIL);
			StringBuilder sb = new StringBuilder();
			for (StackTraceElement se : e.getStackTrace()) {
				sb.append(se.toString());
				sb.append("\n");
			}
			JSONUtils.put(rsObject, "ex", sb.toString());
			log.warn("", e);
		} finally {
			rsWritable.setResult(rsObject.toString());
		}
		return rsWritable;
	}

}
