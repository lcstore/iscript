package com.lezo.iscript.envjs;

import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;

final class NativeWindow extends IdScriptableObject {
	private static final long serialVersionUID = -3816049475691035253L;
	private static final Object STRING_TAG = "Window";

	static void init(Scriptable scope, boolean sealed) {
		NativeWindow obj = new NativeWindow("");
		int maxPrototypeId;
	}

	private NativeWindow(String s) {
	}

	@Override
	public String getClassName() {
		return "Window";
	}
}
