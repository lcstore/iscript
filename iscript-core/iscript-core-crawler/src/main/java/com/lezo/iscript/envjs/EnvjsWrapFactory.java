package com.lezo.iscript.envjs;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lezo.iscript.envjs.dom.DocumentJavaObject;
import com.lezo.iscript.envjs.dom.ElementJavaObject;

public class EnvjsWrapFactory extends WrapFactory {
	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		final Object ret = super.wrap(cx, scope, obj, staticType);
		if (ret instanceof Scriptable) {
			final Scriptable sret = (Scriptable) ret;
			if (sret.getPrototype() == null) {
				sret.setPrototype(new NativeObject());
			}
		}
		return ret;
	}

	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		if (javaObject instanceof Document) {
			return new DocumentJavaObject(scope, javaObject, staticType);
		} else if (javaObject instanceof Node || javaObject instanceof NodeList || javaObject instanceof Element) {
			return new ElementJavaObject(scope, javaObject, staticType);
		} else if (javaObject instanceof String) {
			return Context.toObject(javaObject, scope);
		}
		return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
	}
}
