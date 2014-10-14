package com.lezo.iscript.scope;

import org.mozilla.javascript.Scriptable;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年10月14日
 */
public interface ScriptableFactory {
	public Scriptable createScriptable() throws Exception;
}
