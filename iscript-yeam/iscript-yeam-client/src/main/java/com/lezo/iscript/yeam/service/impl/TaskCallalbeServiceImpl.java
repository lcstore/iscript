package com.lezo.iscript.yeam.service.impl;

import org.apache.log4j.Logger;

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
		rsWritable.setTask(task);
		String type = task.get("type").toString();
		ConfigParser parser = ConfigParserBuffer.getInstance().getParser(type);
		try {
			String rs = parser.doParse(task);
			rsWritable.setResult(rs);
			rsWritable.setStatus(0);
		} catch (Exception e) {
			log.warn("", e);
			rsWritable.setStatus(1);
			rsWritable.addArgs("Exception", e);
		}
		return rsWritable;
	}

}
