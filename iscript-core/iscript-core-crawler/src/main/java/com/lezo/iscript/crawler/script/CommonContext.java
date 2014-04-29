package com.lezo.iscript.crawler.script;

import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

public class CommonContext {
	private static final String charsetName = "UTF-8";
	private static ScriptableObject COMMON_SCRIPTABLE;

	private CommonContext() {
	}

	public static ScriptableObject getCommonScriptable() throws Exception {
		if (COMMON_SCRIPTABLE != null) {
			return COMMON_SCRIPTABLE;
		}
		synchronized (CommonContext.class) {
			if (COMMON_SCRIPTABLE == null) {
				ContextFactory cxFactory = new ContextFactory();
				COMMON_SCRIPTABLE = cxFactory.enterContext().initStandardObjects();
				ScriptContext scx = new ScriptContext(cxFactory, COMMON_SCRIPTABLE);
				ClassLoader loader = CommonContext.class.getClassLoader();
				InputStream in = loader.getResourceAsStream("script/ScriptContext.init");
				Scanner scanner = new Scanner(in, charsetName);
				while (scanner.hasNext()) {
					String name = scanner.nextLine().trim();
					if (name.isEmpty()) {
						continue;
					}
					InputStream scriptStream = null;
					try {
						scriptStream = loader.getResourceAsStream(name);
						InputStreamCaller isCaller = new InputStreamCaller(scriptStream);
						scx.execute(isCaller, null, null);
					} finally {
						IOUtils.closeQuietly(scriptStream);
					}
				}
			}
		}
		return COMMON_SCRIPTABLE;
	}

}
