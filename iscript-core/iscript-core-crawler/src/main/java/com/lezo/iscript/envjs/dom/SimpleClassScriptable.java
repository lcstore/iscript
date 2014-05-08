package com.lezo.iscript.envjs.dom;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SimpleClassScriptable extends NativeJavaObject {
	private static final long serialVersionUID = 709148757230893792L;
	private SimpleClass simpleClass;

	public SimpleClassScriptable(Scriptable scope, Object javaObject, Class<?> staticType) {
		super(scope, javaObject, staticType);
		this.simpleClass = (SimpleClass) javaObject;
	}

	@Override
	public Object get(String name, Scriptable start) {
		// TODO Auto-generated method stub
		return super.get(name, start);
	}

	@Override
	public Object get(int index, Scriptable start) {
		// TODO Auto-generated method stub
		return super.get(index, start);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		simpleClass.valueMap.put(name, value);
		super.put(name, start, value);
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		// TODO Auto-generated method stub
		super.put(index, start, value);
	}

}
