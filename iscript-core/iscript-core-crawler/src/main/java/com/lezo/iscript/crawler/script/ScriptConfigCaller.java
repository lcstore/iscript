package com.lezo.iscript.crawler.script;

import org.json.JSONObject;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.lezo.iscript.yeam.writable.TaskWritable;

public class ScriptConfigCaller implements Callable {
	private TaskWritable task;
	private String config;

	public ScriptConfigCaller(TaskWritable task, String config) {
		super();
		this.task = task;
		this.config = config;
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] callArgs) {
		Object type = task.get("type");
		String name = type.toString();
		String taskArgs = new JSONObject(task.getArgs()).toString();
		String source = String.format("(function(args){%s}(%s));", config, taskArgs);
		return cx.evaluateString(scope, source, name, 0, null);
	}

	public TaskWritable getTask() {
		return task;
	}

	public void setTask(TaskWritable task) {
		this.task = task;
	}

}
