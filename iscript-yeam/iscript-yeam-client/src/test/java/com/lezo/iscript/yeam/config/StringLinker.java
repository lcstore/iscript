package com.lezo.iscript.yeam.config;

import org.json.JSONObject;

import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class StringLinker implements ConfigParser {

	@Override
	public String getName() {
		return "string-link";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject rsObject = new JSONObject();
		String rs = "linker:" + task.get("x") + task.get("y");
		rsObject.put("rs", rs);
		rsObject.put("args", new JSONObject(task.getArgs()));
		return rsObject.toString();
	}

}
