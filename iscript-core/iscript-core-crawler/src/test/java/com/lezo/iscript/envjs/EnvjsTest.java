package com.lezo.iscript.envjs;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class EnvjsTest {

	@Test
	public void test() throws Exception {
		Context cx = EnvjsUtils.enterContext();
		Scriptable scope = EnvjsUtils.initStandardObjects(null);
		String source = FileUtils.readFileToString(new File("src/test/resources/envjs/bdhm.js"));
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
