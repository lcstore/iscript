package com.lezo.iscript.envjs;

import org.mozilla.javascript.ContextFactory;

public class RhinoDebugMain {

	public static void main(String[] args) {
		ContextFactory factory = new ContextFactory();
		// see if the system property has been defined
		String rhino = System.getProperty("rhino.debug");
		if (rhino == null) {
			// if no system property, configure it manually
			rhino = "transport=socket,suspend=y,address=9999";
		}
	}
}
