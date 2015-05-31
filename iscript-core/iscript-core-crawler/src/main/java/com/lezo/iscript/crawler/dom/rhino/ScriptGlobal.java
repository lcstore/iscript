package com.lezo.iscript.crawler.dom.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Environment;

public class ScriptGlobal extends ImporterTopLevel {
	private static final long serialVersionUID = 1L;

	public ScriptGlobal() {
		init(ScriptContextFactory.getFactory(), false);
	}

	public void init(ContextFactory factory, final boolean sealed) {
		factory.call(new ContextAction() {
			public Object run(Context cx) {
				init(cx, sealed);
				return null;
			}
		});
	}

	protected void init(Context cx, boolean sealed) {
		super.initStandardObjects(cx, sealed);
		Environment.defineClass(this);
		Environment environment = new Environment(this);
		defineProperty("environment", environment, ScriptableObject.DONTENUM);
//		defineProperty("window", this, ScriptableObject.DONTENUM);
	}

	@Override
	public String getClassName() {
		return getClass().getSimpleName();
	}
}
