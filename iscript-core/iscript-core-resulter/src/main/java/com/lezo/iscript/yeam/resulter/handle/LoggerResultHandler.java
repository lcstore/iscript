package com.lezo.iscript.yeam.resulter.handle;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.writable.ResultWritable;

public class LoggerResultHandler implements ResultHandle {
	private static Logger log = Logger.getLogger(LoggerResultHandler.class);

	@Override
	public void handle(ResultWritable resultWritable) {
		log.info("taskId:" + resultWritable.getTaskId() + ",status:" + resultWritable.getStatus() + ",rs:"
				+ resultWritable.getResult());

	}

}
