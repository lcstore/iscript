package com.lezo.iscript.crawler.dom.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.Scriptable;

public class ScopeUtils {
	public static Scriptable newObject(final Scriptable parent) {
		return (Scriptable) ContextUtils.doAction(new ContextAction() {
			@Override
			public Object run(Context cx) {
				Scriptable newScope = cx.newObject(parent);
				newScope.setPrototype(parent);
				newScope.setParentScope(null);
				return newScope;
			}
		});
	}
}
