package com.lezo.iscript.yeam.ua;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.envjs.EnvjsUtils;

public class UaFactory {
	private Scriptable scope;

	public String createUa(String uaOpt) {
		if (scope == null) {
			try {
				scope = ScopeFactory.initStandardObjects();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(uaOpt);
		loadUajs(sb);
		addExecjs(sb);
		Context cx = EnvjsUtils.enterContext();
		cx.evaluateString(scope, sb.toString(), "tb.uaexec", 0, null);
		Object uaObject = ScriptableObject.getProperty(scope, "newUa");
		String ua = Context.toString(uaObject);
		return ua;
	}

	protected void addExecjs(StringBuilder sb) {
		sb.append("window.UA_Opt.reload();");
		sb.append("var newUa = eval(UA_Opt.LogVal);");
	}

	protected void loadUajs(StringBuilder sb) {
		InputStream in = UaFactory.class.getClassLoader().getResourceAsStream("js/deua_master.js");
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while (bReader.ready()) {
				String line = bReader.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(bReader);
		}
	}

}
