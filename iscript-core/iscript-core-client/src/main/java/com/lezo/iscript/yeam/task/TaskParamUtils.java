package com.lezo.iscript.yeam.task;

import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskParamUtils {

	@SuppressWarnings("unchecked")
	public static <T> T getOrDefault(TaskWritable taskWritable, String key, T defaultValue) {
		Object value = taskWritable.get(key);
		return (T) (value == null ? defaultValue : value);
	}
}
