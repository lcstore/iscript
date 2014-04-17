package com.lezo.iscript.yeam.tasker.buffer;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public class TypeUtils {

	public static String getName(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		int index = name.lastIndexOf('.');
		String type = index < 0 ? name : name.substring(0, index);
		return type;
	}

	public static String getType(File name) {
		return getName(name.getName());
	}
}
