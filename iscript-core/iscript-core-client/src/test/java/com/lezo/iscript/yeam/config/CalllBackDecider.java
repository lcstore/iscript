package com.lezo.iscript.yeam.config;

import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class CalllBackDecider implements ConfigParser {
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		ResultWritable rWritable = (ResultWritable) task.get("ResultWritable");
		String type = rWritable.getType();
		if (type.endsWith("Product")) {
			return null;
		}
		return rWritable.getResult();
	}
}
