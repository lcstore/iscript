package com.lezo.iscript.crawler.script;

import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.scope.ScriptableUtils;

public class CommonContext {
	public static ScriptableObject getCommonScriptable() throws Exception {
		return (ScriptableObject) ScriptableUtils.getCoreScriptable();
	}

}
