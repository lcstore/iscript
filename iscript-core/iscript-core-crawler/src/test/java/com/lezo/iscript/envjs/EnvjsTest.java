package com.lezo.iscript.envjs;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.script.CommonContext;

public class EnvjsTest {

	@Test
	public void test() throws Exception {
		Context cx = EnvjsUtils.enterContext();
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
//		String source = FileUtils.readFileToString(new File("src/test/resources/envjs/__guid.js"));
		String source = FileUtils.readFileToString(new File("src/test/resources/envjs/jdbbs.js"));
		cx.evaluateString(scope, source, "cmd", 0, null);
//		source = FileUtils.readFileToString(new File("src/test/resources/envjs/analytics.js"));
//		cx.evaluateString(scope, source, "cmd", 0, null);

		System.out.println("ua:------");

	}

	@Test
	public void testBase() throws Exception {
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects(null);
		cx.setWrapFactory(new EnvjsWrapFactory());
		StringBuffer sb = new StringBuffer();
		sb.append("var log = function(msg){");
		sb.append("if(msg){");
		sb.append("	java.lang.System.out.println(msg);");
		sb.append("}else {");
		sb.append("	java.lang.System.err.println(\"null\");");
		sb.append("}");
		sb.append("};");
		sb.append("simpleObject.age=100;");
		sb.append("log('simpleObject.age:'+simpleObject.age);");
		sb.append("log('simpleObject.name:'+simpleObject.name);");
		String source = sb.toString();
		cx.evaluateString(scope, source, "cmd", 0, null);
		// String uaString = "UA_Opt.reload(); var sua = sm.ua;";
		// String uaString =
		// "for(var i in UA_Opt){java.lang.System.out.println(i+'='+UA_Opt[i]);}";
		// cx.evaluateString(scope, uaString, "cmd", 0, null);

		// Object rs = ScriptableObject.getProperty(scope, "sua");
		// String ua = Context.toString(rs);
		// System.out.println("ua:------" + ua);
		System.out.println("ua:------");

	}

}
