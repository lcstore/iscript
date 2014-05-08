package com.lezo.iscript.envjs.dom;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NodeScriptable extends NativeJavaObject {
	private static final long serialVersionUID = 709148757230893792L;
	private Node node;

	public NodeScriptable(Scriptable scope, Object javaObject, Class<?> staticType) {
		super(scope, javaObject, staticType,true);
		this.node = (Node) javaObject;
	}

	@Override
	public Object get(String name, Scriptable start) {
		Object result = super.get(name, start);
		if (result == null && node.hasAttributes()) {
			NamedNodeMap attrMap = node.getAttributes();
			Node attrNode = attrMap.getNamedItem(name);
			if (attrNode != null) {
				result = attrNode.getNodeValue();
			}
		}
		return result;
	}

	@Override
	public Object get(int index, Scriptable start) {
		// TODO Auto-generated method stub
		return super.get(index, start);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		Element e = (Element) node;
		e.setAttribute(name, value == null ? null : value.toString());
		super.put(name, start, value);
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		// TODO Auto-generated method stub
		super.put(index, start, value);
	}

}
