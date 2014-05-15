package com.lezo.iscript.envjs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
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

import com.lezo.iscript.envjs.dom.DocumentAdapt;
import com.lezo.iscript.envjs.dom.DocumentJavaObject;
import com.lezo.iscript.envjs.dom.ElementJavaObject;
import com.lezo.iscript.envjs.window.LocationAdapt;
import com.lezo.iscript.envjs.window.NavigatorAdapt;
import com.lezo.iscript.envjs.window.WindowAdapt;

public class DomParser {
	private Scriptable globalScope;

	@Test
	public void test() throws Exception {
		String content = FileUtils.readFileToString(new File("src/main/resources/envjs/test.html"));
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream is = new ByteArrayInputStream(content.getBytes());
		Document document = documentBuilder.parse(is);
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		String source = FileUtils.readFileToString(new File("src/main/resources/envjs/bdhm.js"));
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
				if (javaObject instanceof Document) {
					return new DocumentJavaObject(scope, javaObject, staticType);
				} else if (javaObject instanceof Node || javaObject instanceof NodeList
						|| javaObject instanceof Element) {
					return new ElementJavaObject(scope, javaObject, staticType);
				}else if(javaObject instanceof String){
					return Context.toObject(javaObject, scope);
				}
				return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
			}
		});
		WindowAdapt window = new WindowAdapt();
		LocationAdapt location = new LocationAdapt();
		location.setHref("http://shu.taobao.com/");
		DocumentAdapt documentAdapt = new DocumentAdapt(document, location);
		documentAdapt.setUserData("g_scope_key", scope, null);
		NavigatorAdapt navigator = new NavigatorAdapt();
		window.setLocation(location);
		window.setDocument(documentAdapt);
		window.setNavigator(navigator);
		ScriptableObject.putProperty(scope, "window", Context.toObject(window, scope));
		ScriptableObject.putProperty(scope, "document", Context.toObject(documentAdapt, scope));
		ScriptableObject.putProperty(scope, "navigator", Context.toObject(navigator, scope));
		ScriptableObject.putProperty(scope, "location", Context.toObject(location, scope));
		cx.evaluateString(scope, source, "cmd", 0, null);

//		 String uaString = "UA_Opt.reload(); var sua = sm.ua;";
//		String uaString = "for(var i in UA_Opt){java.lang.System.out.println(i+'='+UA_Opt[i]);}";
//		 cx.evaluateString(scope, uaString, "cmd", 0, null);

//		Object rs = ScriptableObject.getProperty(scope, "sua");
//		String ua = Context.toString(rs);
//		System.out.println("ua:------" + ua);
		System.out.println("ua:------");
		
	}

	public Scriptable getGlobalScope() {
		return globalScope;
	}

	public void setGlobalScope(Scriptable globalScope) {
		this.globalScope = globalScope;
	}
}
