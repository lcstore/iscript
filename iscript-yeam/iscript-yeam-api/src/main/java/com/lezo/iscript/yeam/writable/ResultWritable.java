package com.lezo.iscript.yeam.writable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ResultWritable implements Serializable {
	private static final long serialVersionUID = 3628691351919899205L;
	private TaskWritable task;
	private int status;
	private String result;
	private Map<String, Object> args = new HashMap<String, Object>();

	public TaskWritable getTask() {
		return task;
	}

	public void setTask(TaskWritable task) {
		this.task = task;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Map<String, Object> getArgs() {
		return args;
	}

	public Object addArgs(String key, Object value) {
		return this.args.put(key, value);
	}

}
