package com.lezo.iscript.yeam.ua;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.envjs.EnvjsUtils;

public abstract class UaFactory {

	public void initUa(String uaOpt, Scriptable scope) {
		Context cx = EnvjsUtils.enterContext();
		cx.evaluateString(scope, uaOpt, "tb.uaOpt", 0, null);
		cx.evaluateString(scope, loadUajs(), "tb.ua", 0, null);
		Context.exit();
	}

	public String getUa(String callString, Scriptable scope) {
		Context cx = EnvjsUtils.enterContext();
		StringBuilder sb = new StringBuilder();
		sb.append(callString);
		sb.append("var newUa = eval(UA_Opt.LogVal);");
		cx.evaluateString(scope, sb.toString(), "tb.uaexec", 0, null);
		Object uaObject = ScriptableObject.getProperty(scope, "newUa");
		String ua = Context.toString(uaObject);
		Context.exit();
		return ua;
	}

	protected abstract String loadUajs();

	public abstract String getUaOpt();

}
