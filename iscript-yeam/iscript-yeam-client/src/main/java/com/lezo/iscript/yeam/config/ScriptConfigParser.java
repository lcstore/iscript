package com.lezo.iscript.yeam.config;

import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

import com.lezo.iscript.crawler.script.CommonContext;
import com.lezo.iscript.crawler.script.ScriptConfigCaller;
import com.lezo.iscript.crawler.script.ScriptContext;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ScriptConfigParser implements ConfigParser {
	private String config;

	public ScriptConfigParser(String config) {
		super();
		this.config = config;
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		ScriptConfigCaller scCaller = new ScriptConfigCaller(task, config);
		ScriptContext scx = new ScriptContext(new ContextFactory(), CommonContext.getCommonScriptable());
		Object[] argsObjects = toObjects(task.getArgs());
		Object rs = scx.execute(scCaller, scx.getScope(), argsObjects);
		return Context.toString(rs);
	}

	private Object[] toObjects(Map<String, Object> args) {
		if (args == null) {
			return new Object[0];
		}
		Object[] objects = new Object[args.size()];
		int i = -1;
		for (Entry<String, Object> entry : args.entrySet()) {
			objects[++i] = entry.getValue();
		}
		return objects;
	}

	@Override
	public String getName() {
		return null;
	}

}
