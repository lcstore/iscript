package com.lezo.iscript.yeam.ua;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.script.CommonContext;
import com.lezo.iscript.envjs.EnvjsUtils;

public class ScopeFactory {
	private static ScriptableObject parent;
	static {
		try {
			parent = CommonContext.getCommonScriptable();
			parent = (ScriptableObject) EnvjsUtils.initStandardObjects(parent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Scriptable initStandardObjects() throws Exception {
		return EnvjsUtils.initStandardObjects(parent);
	}
}
