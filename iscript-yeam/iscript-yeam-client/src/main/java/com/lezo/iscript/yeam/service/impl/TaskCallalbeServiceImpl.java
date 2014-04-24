package com.lezo.iscript.yeam.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.lezo.iscript.yeam.ResultConstant;
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
		rsWritable.setTaskId(task.getId());
		Object oBatchId = task.get("batch_id");
		if (oBatchId != null) {
			Long batchId = Long.valueOf(oBatchId.toString());
			rsWritable.setBatchId(batchId);
		}
		String type = task.get("type").toString();
		ConfigParser parser = ConfigParserBuffer.getInstance().getParser(type);
		JSONObject rsObject = new JSONObject();
		try {
			String rs = parser.doParse(task);
			if (StringUtils.isEmpty(rs)) {
				String argsString = getJsonArgs(rs);
			}
			rsWritable.setResult(rs);
			rsWritable.setStatus(ResultConstant.RESULT_SUCCESS);
		} catch (Exception e) {
			log.warn("", e);
			rsWritable.setStatus(ResultConstant.RESULT_FAIL);
		}
		return rsWritable;
	}

	private String getJsonArgs(String rs) {
		rs = rs.trim();
		if (!rs.startsWith("{")) {
			rs = "{" + rs;
		}
		if (!rs.endsWith("}")) {
			rs = rs + "}";
		}
		return rs;
	}

}
