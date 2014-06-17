package com.lezo.iscript.envjs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.w3c.dom.Document;

import com.lezo.iscript.envjs.dom.DocumentAdapt;
import com.lezo.iscript.envjs.window.HistoryAdapt;
import com.lezo.iscript.envjs.window.LocationAdapt;
import com.lezo.iscript.envjs.window.NavigatorAdapt;
import com.lezo.iscript.envjs.window.ScreenAdapt;

public class EnvjsUtils {
	private static final WrapFactory WRAP_FACTORY = new EnvjsWrapFactory();

	public static Context enterContext() {
		Context cx = Context.enter();
		cx.setWrapFactory(WRAP_FACTORY);
		return cx;
	}

	public static Scriptable initStandardObjects(ScriptableObject parent) throws Exception {
		Context cx = enterContext();
		Scriptable scope = null;
		try {
			scope = cx.initStandardObjects(parent);
			initEnvFromJava(scope);
			initEnvFromScript(cx, scope);
		} finally {
			Context.exit();
		}
		return scope;
	}

	private static void initEnvFromJava(Scriptable scope) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		String content = "<html><head><script type=\"text/javascript\"></script></head><body></body></html>";
		InputStream is = new ByteArrayInputStream(content.getBytes());
		// create standar document.
		Document document = documentBuilder.parse(is);
		// create adapt window,location,document
		LocationAdapt location = new LocationAdapt();
		DocumentAdapt documentAdapt = new DocumentAdapt(document, location);
		documentAdapt.setUserData("g_scope_key", scope, null);
		// init host script object
		ScriptableObject.putProperty(scope, "document", Context.toObject(documentAdapt, scope));
		ScriptableObject.putProperty(scope, "navigator", Context.toObject(new NavigatorAdapt(), scope));
		ScriptableObject.putProperty(scope, "location", Context.toObject(location, scope));
		ScriptableObject.putProperty(scope, "screen", Context.toObject(new ScreenAdapt(), scope));
		ScriptableObject.putProperty(scope, "history", Context.toObject(new HistoryAdapt(null), scope));
	}

	private static void initEnvFromScript(Context cx, Scriptable scope) throws Exception {
		InputStream in = null;
		Reader reader = null;
		try {
			ClassLoader loader = EnvjsUtils.class.getClassLoader();
			in = loader.getResourceAsStream("script/env.core.js");
			reader = new InputStreamReader(in);
			cx.evaluateReader(scope, reader, "envjs", 0, null);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
	}
}
