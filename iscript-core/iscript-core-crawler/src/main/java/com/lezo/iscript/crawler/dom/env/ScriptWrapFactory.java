package com.lezo.iscript.crawler.dom.env;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class ScriptWrapFactory extends WrapFactory {

	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		Scriptable wrap;
		wrap = new ScriptJavaObject(scope, javaObject, staticType);
		return wrap;
//		return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
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
