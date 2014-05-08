package com.lezo.iscript.envjs.dom;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.NodeList;

public class NodeListScriptable extends NativeJavaObject {
	private static final long serialVersionUID = 709148757230893792L;
	private NodeList nodeList;

	public NodeListScriptable(Scriptable scope, Object javaObject, Class<?> staticType) {
		super(scope, javaObject, staticType);
		this.nodeList = (NodeList) javaObject;
	}

	@Override
	public Object get(int index, Scriptable start) {
		return nodeList.item(index);
	}
}
