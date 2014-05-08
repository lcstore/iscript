package com.lezo.iscript.envjs.dom;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Element;

public class ElementScriptable extends NativeJavaObject {
	private static final long serialVersionUID = 709148757230893792L;
	private Element element;

	public ElementScriptable(Scriptable scope, Object javaObject, Class<?> staticType) {
		super(scope, javaObject, staticType);
		this.element = (Element) javaObject;
	}

	@Override
	public Object get(String name, Scriptable start) {
		Object result = super.get(name, start);
		if (result == null) {
			result = element.getAttribute(name);
		}
		return result;
	}

	@Override
	public Object get(int index, Scriptable start) {
		// TODO Auto-generated method stub
		return super.get(index, start);
	}

}
