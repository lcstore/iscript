package com.lezo.iscript.crawler.dom.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;

public class ScriptWrapFactory extends WrapFactory {

	public ScriptWrapFactory() {
		// String,Boolean convert to js native
		setJavaPrimitiveWrap(false);
	}

	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		if (javaObject == null || javaObject == Undefined.instance) {
			return null;
		}
		Scriptable wrap = new ScriptJavaObject(scope, javaObject, staticType);
		return wrap;
		// return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
	}

	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		// TODO Auto-generated method stub
		return super.wrap(cx, scope, obj, staticType);
	}

	@Override
	public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
		// TODO Auto-generated method stub
		return super.wrapNewObject(cx, scope, obj);
	}

}
