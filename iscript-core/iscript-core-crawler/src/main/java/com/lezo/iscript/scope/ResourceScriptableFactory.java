package com.lezo.iscript.scope;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年10月14日
 */
public class ResourceScriptableFactory implements ScriptableFactory {
	private String[] codePaths;
	private ScriptableObject parent;

	public ResourceScriptableFactory(ScriptableObject parent, String... codePaths) {
		super();
		this.parent = parent;
		this.codePaths = codePaths;
	}

	public Scriptable createScriptable() throws Exception {
		Scriptable scope = null;
		InputStream in = null;
		Reader reader = null;
		try {
			Context cx = Context.enter();
			scope = cx.initStandardObjects(this.parent);
			ClassLoader loader = ResourceScriptableFactory.class.getClassLoader();
			int size = codePaths.length;
			for (int i = 0; i < size; i++) {
				String path = codePaths[i];
				in = loader.getResourceAsStream(path);
				reader = new InputStreamReader(in);
				cx.evaluateReader(scope, reader, "<cmd>", 0, null);
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(reader);
			}
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
			Context.exit();
		}
		return scope;
	}
}
