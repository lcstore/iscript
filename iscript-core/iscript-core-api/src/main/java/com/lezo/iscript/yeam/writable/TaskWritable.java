package com.lezo.iscript.yeam.writable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TaskWritable implements Serializable {
	private static final long serialVersionUID = 6494574310364649978L;
	public static final int LEVEL_MIN = 1;
	public static final int LEVEL_MAX = 10000;
	private Long id;
	private Map<String, Object> args = new HashMap<String, Object>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<String, Object> getArgs() {
		return args;
	}

	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}

	public void put(String key, Object value) {
		args.put(key, value);
	}

	public Object get(String key) {
		return args.get(key);
	}

}
