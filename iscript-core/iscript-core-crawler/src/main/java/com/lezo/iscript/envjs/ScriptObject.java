package com.lezo.iscript.envjs;

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

public class ScriptObject extends NativeJavaObject {
	private static final long serialVersionUID = 2792641870019524955L;
	private Map<String, Object> fieldMap = new HashMap<String, Object>();

	public ScriptObject(Scriptable scope) {
		super(scope, new Object(), Object.class, false);
	}

	@Override
	public Object get(String name, Scriptable start) {
		if (has(name, start)) {
			return super.get(name, start);
		}
		if (fieldMap.containsKey(name)) {
			return fieldMap.get(name);
		}
		return Scriptable.NOT_FOUND;
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		if (has(name, start)) {
			super.put(name, start, value);
			return;
		}
		if (value == null || value == Scriptable.NOT_FOUND) {
			fieldMap.remove(name);
		} else {
			fieldMap.put(name, value);
		}
	}

	@Override
	public String getClassName() {
		return "ScriptObject";
	}

}
