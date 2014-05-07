package com.lezo.iscript.envjs.dom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.w3c.dom.Document;

public class DomParser {

	@Test
	public void test() throws Exception {
		String content = FileUtils.readFileToString(new File("src/main/resources/envjs/test.html"));
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		InputStream is = new ByteArrayInputStream(content.getBytes());
		String uri = "http://www.baidu.com/";
		Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		String source = FileUtils.readFileToString(new File("src/main/resources/envjs/jsDocument.js"));

		cx.setWrapFactory(new WrapFactory() {
			@Override
			public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
				final Object ret = super.wrap(cx, scope, obj, staticType);
				if (ret instanceof Scriptable) {
					final Scriptable sret = (Scriptable) ret;
					if (sret.getPrototype() == null) {
						sret.setPrototype(new NativeObject());
					}
				}
				return ret;
			}
		});
		LocationScriptObject location = new LocationScriptObject(scope, new Object());
		ScriptableObject.putProperty(scope, "document", new DocumentScriptObject(location, scope, document));
		cx.evaluateString(scope, source, "cmd", 0, null);
	}
}
