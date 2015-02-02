package com.lezo.iscript.utils;

public interface ValueGetter {
	boolean hasKey(String name);

	Object getValue(String name, Object source);
}
