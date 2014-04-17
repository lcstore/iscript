package com.lezo.iscript.yeam.service;

import com.lezo.iscript.yeam.writable.TaskWritable;

public interface ConfigParser {
	String getName();

	String doParse(TaskWritable task) throws Exception;
}
