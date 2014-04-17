package com.lezo.iscript.yeam.config;

import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class StringLinker implements ConfigParser {

	@Override
	public String getName() {
		return "string-link";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		return "linker:" + task.get("x") + task.get("y");
	}

}
