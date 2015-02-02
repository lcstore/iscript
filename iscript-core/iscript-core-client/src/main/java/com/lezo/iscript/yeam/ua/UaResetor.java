package com.lezo.iscript.yeam.ua;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class UaResetor {
	private Scriptable scope;
	private StringBuilder builder = new StringBuilder();

	public UaResetor(Scriptable scope) {
		super();
		this.scope = scope;
	}

	public UaResetor append(InputStream in) throws IOException {
		if (in.available() < 1) {
			return this;
		}
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while (bReader.ready()) {
				String line = bReader.readLine();
				if (line == null) {
					break;
				}
				builder.append(line);
				builder.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(bReader);
		}
		return this;
	}

	public UaResetor append(String jsCode) {
		if (!StringUtils.isEmpty(jsCode)) {
			builder.append(jsCode);
			builder.append("\n");
		}
		return this;
	}

	public UaResetor callScript(Context cx) {
		if (builder.length() > 0) {
			cx.evaluateString(scope, builder.toString(), "ua", 0, null);
			builder = new StringBuilder();
		}
		return this;
	}

	public String getUa() {
		String source = "var newUa = eval(UA_Opt.LogVal);";
		try {
			Context cx = Context.enter();
			cx.evaluateString(scope, source, "ua", 0, null);
			Object uaObject = ScriptableObject.getProperty(scope, "newUa");
			if (uaObject == null || uaObject == ScriptableObject.NOT_FOUND) {
				return null;
			}
			return Context.toString(uaObject);
		} finally {
			Context.exit();
		}
	}
}
