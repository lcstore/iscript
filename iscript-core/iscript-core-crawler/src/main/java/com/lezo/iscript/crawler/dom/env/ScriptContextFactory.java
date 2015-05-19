package com.lezo.iscript.crawler.dom.env;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class ScriptContextFactory extends ContextFactory {
	private static ContextFactory scriptFactory = new ScriptContextFactory();
	static {
//		ContextFactory.initGlobal(getGlobal());
		ContextFactory.getGlobal().enterContext().setWrapFactory(new ScriptWrapFactory());
	}

	@Override
	protected boolean hasFeature(Context cx, int featureIndex) {
		// if (Context.FEATURE_ENHANCED_JAVA_ACCESS == featureIndex) {
		// return true;
		// }
		return super.hasFeature(cx, featureIndex);
	}

	@Override
	public Context enterContext() {
		return super.enterContext();
	}

	public static ContextFactory getGlobal() {
		return scriptFactory;
	}
}
