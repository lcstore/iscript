package com.lezo.iscript.envjs;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

public class PrototypeJavaObject extends NativeJavaObject {
	private static final long serialVersionUID = 2792641870019524955L;

	public PrototypeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
		super(scope, javaObject, staticType);
	}

	@Override
	public Object get(String name, Scriptable start) {
		Object result = null;
		if (has(name, start)) {
			result = super.get(name, start);
		} else if (prototype != null) {
			result = prototype.get(name, start);
		}
		return doReturn(result, start);
	}

	@Override
	public Object get(int index, Scriptable start) {
		Object result = null;
		if (has(index, start)) {
			result = super.get(index, start);
		} else if (prototype != null) {
			result = prototype.get(index, start);
		}
		return doReturn(result, start);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		if (has(name, start)) {
			super.put(name, start, value);
		} else if (prototype != null) {
			prototype.put(name, start, value);
		}
		// if (value instanceof BaseFunction) {
		// if (javaObject instanceof Document || javaObject instanceof
		// WindowAdapt) {
		// if (!ScriptableObject.hasProperty(start, name) &&
		// !ScriptableObject.hasProperty(parent, name)) {
		// ScriptableObject.putProperty(start, name, value);
		// }
		// }
		// }
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		if (has(index, start)) {
			super.put(index, start, value);
		} else if (prototype != null) {
			prototype.put(index, start, value);
		}
	}

	private Object doReturn(Object result, Scriptable scope) {
		if (result != null) {
			return result;
		}
		return Scriptable.NOT_FOUND;
	}
}
