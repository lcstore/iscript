package com.lezo.iscript.crawler.dom.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class ScriptContextFactory extends ContextFactory {
	private static ContextFactory factory = new ScriptContextFactory();
	private boolean useDynamicScope = true;
	private boolean enhancedJavaAccess = false;

	private ScriptContextFactory() {
	}

	@Override
	protected boolean hasFeature(Context cx, int featureIndex) {
		if (Context.FEATURE_ENHANCED_JAVA_ACCESS == featureIndex) {
			return isEnhancedJavaAccess();
		} else if (featureIndex == Context.FEATURE_DYNAMIC_SCOPE) {
			return isUseDynamicScope();
		}
		return super.hasFeature(cx, featureIndex);
	}

	@Override
	protected Context makeContext() {
		Context cx = super.makeContext();
		cx.setWrapFactory(new ScriptWrapFactory());
		return cx;
	}

	public boolean isUseDynamicScope() {
		return useDynamicScope;
	}

	public void setUseDynamicScope(boolean useDynamicScope) {
		this.useDynamicScope = useDynamicScope;
	}

	public boolean isEnhancedJavaAccess() {
		return enhancedJavaAccess;
	}

	public void setEnhancedJavaAccess(boolean enhancedJavaAccess) {
		this.enhancedJavaAccess = enhancedJavaAccess;
	}

	public static ContextFactory getFactory() {
		return factory;
	}

}
