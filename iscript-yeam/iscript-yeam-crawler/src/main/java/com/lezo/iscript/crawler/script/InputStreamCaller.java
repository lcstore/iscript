package com.lezo.iscript.crawler.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class InputStreamCaller implements Callable {
	private InputStream inputStream;
	private String charsetName;

	public InputStreamCaller(InputStream inputStream) {
		this(inputStream, "UTF-8");
	}

	public InputStreamCaller(InputStream inputStream, String charsetName) {
		super();
		this.inputStream = inputStream;
		this.charsetName = charsetName;
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] callArgs) {
		try {
			// InputStream inputStream =
			// Thread.currentThread().getContextClassLoader()
			// .getResourceAsStream("com/yihaodian/pis/javascriptcrawler/resources/json2.js");
			InputStreamReader reader = new InputStreamReader(inputStream, charsetName);
			return cx.evaluateReader(scope, reader, getClass().getSimpleName(), 0, null);
		} catch (IOException e) {
		}
		return null;
	}
}
