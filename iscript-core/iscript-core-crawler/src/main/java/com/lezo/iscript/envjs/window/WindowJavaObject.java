package com.lezo.iscript.envjs.window;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class WindowJavaObject extends NativeJavaObject {
	private static final long serialVersionUID = -1399487018950636098L;

	public WindowJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
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
		return doReturn(result);
	}

	@Override
	public Object get(int index, Scriptable start) {
		Object result = null;
		if (has(index, start)) {
			result = super.get(index, start);
		} else if (prototype != null) {
			result = prototype.get(index, start);
		}
		return doReturn(result);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		if (has(name, start)) {
			super.put(name, start, value);
		} else if (prototype != null) {
			prototype.put(name, start, value);
			if (value instanceof Function) {
				// global fun
				ScriptableObject.putProperty(this.parent, name, value);
			}
		}
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		if (has(index, start)) {
			super.put(index, start, value);
		} else if (prototype != null) {
			prototype.put(index, start, value);
		}
	} 

	private Object doReturn(Object result) {
		if (result != null) {
			return result;
		}
		return Scriptable.NOT_FOUND;
	}
}
