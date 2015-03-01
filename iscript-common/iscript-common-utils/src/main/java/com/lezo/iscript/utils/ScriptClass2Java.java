package com.lezo.iscript.utils;

public class ScriptClass2Java {

	public static Integer getInteger(String args) {
		if (args == null) {
			return null;
		}
		return Integer.parseInt(args);
	}

	public static Short getShort(String args) {
		if (args == null) {
			return null;
		}
		return Short.parseShort(args);
	}

	public static Long getLong(String args) {
		if (args == null) {
			return null;
		}
		return Long.parseLong(args);
	}

	public static Float getFloat(String args) {
		if (args == null) {
			return null;
		}
		return Float.parseFloat(args);
	}

	public static Byte getByte(String args) {
		if (args == null) {
			return null;
		}
		return Byte.parseByte(args);
	}
}
