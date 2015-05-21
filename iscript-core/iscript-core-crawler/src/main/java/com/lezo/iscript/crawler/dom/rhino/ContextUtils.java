package com.lezo.iscript.crawler.dom.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class ContextUtils {
	static {
		ContextFactory.initGlobal(ScriptContextFactory.getFactory());
	}

	private static Context enter() {
		return ScriptContextFactory.getFactory().enterContext();
	}

	public static Object call(final Scriptable scope, final Scriptable thisScope, final Function function, Object[] args) {
		try {
			Context cx = enter();
			return function.call(cx, scope, thisScope, args);
		} finally {
			Context.exit();
		}
	}

	public static Object call(final Scriptable scope, final String source, final String name) {
		try {
			Context cx = enter();
			return cx.evaluateString(scope, source, name, 0, null);
		} finally {
			Context.exit();
		}
	}

	public static Object doAction(ContextAction action) {
		Context ctx = enter();
		try {
			return action.run(ctx);
		} finally {
			Context.exit();
		}
	}
}
