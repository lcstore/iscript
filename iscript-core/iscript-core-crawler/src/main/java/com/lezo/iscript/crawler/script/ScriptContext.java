package com.lezo.iscript.crawler.script;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ScriptContext {
	private ContextFactory factory;
	private Scriptable scope;

	public ScriptContext() {
		this(new ContextFactory(), null);
	}

	public ScriptContext(ContextFactory cxFactory, ScriptableObject parent) {
		super();
		this.factory = cxFactory;
		this.scope = this.factory.enterContext().initStandardObjects(parent);
	}

	public Object execute(Callable callable, Scriptable thisObj, Object[] args) throws Exception {
		try {
			Context cx = this.factory.enterContext();
			return callable.call(cx, scope, thisObj, args);
		} catch (Exception e) {
			throw e;
		} finally {
			Context.exit();
		}
	}

	public void doDefaultInit() throws Exception {
		ClassLoader loader = ScriptContext.class.getClassLoader();
		InputStream in = loader.getResourceAsStream("script/ScriptContext.init");
		BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = bReader.readLine()) != null) {
			String name = line.trim();
			if (name.isEmpty()) {
				continue;
			}
			InputStream scriptStream = null;
			try {
				scriptStream = loader.getResourceAsStream(name);
				InputStreamCaller isCaller = new InputStreamCaller(scriptStream);
				execute(isCaller, null, null);
			} catch (Exception e) {
				throw e;
			} finally {
				IOUtils.closeQuietly(scriptStream);
			}
		}
	}

	public ContextFactory getFactory() {
		return factory;
	}

	public void setFactory(ContextFactory factory) {
		this.factory = factory;
	}

	public Scriptable getScope() {
		return scope;
	}

	public void setScope(Scriptable scope) {
		this.scope = scope;
	}
}
