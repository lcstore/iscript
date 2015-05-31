package com.lezo.iscript.crawler.dom.rhino;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ScriptJavaObject extends NativeJavaObject {
	private static final long serialVersionUID = 2792641870019524955L;

	public ScriptJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
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
		if (result == null || result == Scriptable.NOT_FOUND) {
			Object originObject = unwrap();
			if (Element.class.isInstance(originObject)) {
				Element element = (Element) originObject;
				result = element.getAttribute(name);
				return doReturn(result, start);
			}
		}
		return doReturn(result, start);
	}

	@Override
	public Object get(int index, Scriptable start) {
		Object result = null;
		Object originObject = unwrap();
		if (NodeList.class.isInstance(originObject)) {
			NodeList nl = (NodeList) originObject;
			return doReturn(nl.item(index), start);
		}
		if (has(index, start)) {
			result = super.get(index, start);
		} else if (prototype != null) {
			result = prototype.get(index, start);
		}
		return doReturn(result, start);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		boolean isDone = false;
		if (has(name, start)) {
			super.put(name, start, value);
			isDone = true;
		}
		if (!isDone) {
			Object originObject = unwrap();
			if (Element.class.isInstance(originObject)) {
				Element element = (Element) originObject;
				element.setAttribute(name, value.toString());
				isDone = true;
			}
		}
		if (!isDone && prototype != null) {
			prototype.put(name, start, value);
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

	private Object doReturn(Object result, Scriptable scope) {
		if (result != null) {
			return result;
		}
		return Scriptable.NOT_FOUND;
	}
}
