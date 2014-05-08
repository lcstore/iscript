package com.lezo.iscript.envjs.dom;

import java.util.ArrayList;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

public class ArrayListScriptable extends NativeJavaObject {
	private static final long serialVersionUID = 709148757230893792L;
	private ArrayList<?> arrayList;

	public ArrayListScriptable(Scriptable scope, Object javaObject, Class<?> staticType) {
		super(scope, javaObject, staticType);
		this.arrayList = (ArrayList<?>) javaObject;
	}

	@Override
	public Object get(int index, Scriptable start) {
		return arrayList.get(index);
	}
}
