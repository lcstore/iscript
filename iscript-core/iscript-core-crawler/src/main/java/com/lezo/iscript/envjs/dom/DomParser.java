package com.lezo.iscript.envjs.dom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomParser {

	@Test
	public void test() throws Exception {
		String content = FileUtils.readFileToString(new File("src/main/resources/envjs/test.html"));
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		InputStream is = new ByteArrayInputStream(content.getBytes());
		String uri = "http://www.baidu.com/";
		Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
		document = documentBuilderFactory.newDocumentBuilder().parse(is);
		System.out.println(document.getClass().getName());
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

			@Override
			public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
				if (javaObject instanceof ArrayList<?>) {
					return new ArrayListScriptable(scope, javaObject, staticType);
				} else if (javaObject instanceof Document) {
					return new DocumentScriptable(scope, javaObject, staticType);
				} else if (javaObject instanceof Node || javaObject instanceof NodeList
						|| javaObject instanceof Element) {
					return new ElementScriptable(scope, javaObject, staticType);
				} else if (javaObject instanceof SimpleClass) {
					return new SimpleClassScriptable(scope, javaObject, staticType);
				}
				return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
			}
		});
		LocationScript location = new LocationScript();
		DocumentScript documentScript = new DocumentScript(document, location);
		ScriptableObject.putProperty(scope, "document", document);
		ScriptableObject.putProperty(scope, "location", location);
		SimpleClass simpleClass = new SimpleClass();
		ScriptableObject.putProperty(scope, "SimpleClass", Context.toObject(simpleClass, scope));
		List<String> argsList = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			argsList.add("args:" + i);
		}
		ScriptableObject.putProperty(scope, "argsList", Context.toObject(argsList, scope));
		cx.evaluateString(scope, source, "cmd", 0, null);
		NodeList scriptList = document.getElementsByTagName("script");
		for (int i = 0; i < scriptList.getLength(); i++) {
			Node scriptEle = scriptList.item(i);
			System.out.println(scriptEle.getNodeName());
		}
		System.out.println(scriptList.getLength());
	}
}
