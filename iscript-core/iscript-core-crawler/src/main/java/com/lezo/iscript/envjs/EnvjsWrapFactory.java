package com.lezo.iscript.envjs;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lezo.iscript.envjs.dom.DocumentAdapt;
import com.lezo.iscript.envjs.dom.ElementJavaObject;

/**
 * Memory leak
 * 
 * @author lezo
 *
 */
@Deprecated
public class EnvjsWrapFactory extends WrapFactory {
	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		final Object ret = super.wrap(cx, scope, obj, staticType);
		if (ret instanceof Scriptable) {
			final Scriptable sret = (Scriptable) ret;
			if (sret.getPrototype() == null) {
				ScriptObject scriptObject = ScriptObjectFactory.getScriptObject(obj, scope);
				sret.setPrototype(scriptObject);
			}
		}
		return ret;
	}

	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		if (javaObject instanceof DocumentAdapt) {
			// return new DocumentJavaObject(scope, javaObject, staticType);
		} else if (javaObject instanceof Node || javaObject instanceof NodeList || javaObject instanceof Element) {
			return new ElementJavaObject(scope, javaObject, staticType);
		} else if (javaObject instanceof String) {
			return Context.toObject(javaObject, scope);
		} else if (javaObject.getClass().getName().startsWith("org.mozilla.")) {
		}
		// return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
		return new PrototypeJavaObject(scope, javaObject, staticType);
	}
}
