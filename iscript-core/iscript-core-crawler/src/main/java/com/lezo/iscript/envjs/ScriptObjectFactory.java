package com.lezo.iscript.envjs;

import java.util.concurrent.ConcurrentHashMap;

import org.mozilla.javascript.Scriptable;

public class ScriptObjectFactory {
	private static final ConcurrentHashMap<Object, ScriptObject> scriptObjectMap = new ConcurrentHashMap<Object, ScriptObject>();

	public static ScriptObject getScriptObject(Object javaObject, Scriptable scope) {
		ScriptObject scriptObject = scriptObjectMap.get(javaObject);
		if (scriptObject == null) {
			synchronized (scriptObjectMap) {
				scriptObject = new ScriptObject(scope);
				scriptObjectMap.putIfAbsent(javaObject, scriptObject);
			}
		}
		return scriptObject;
	}
}
