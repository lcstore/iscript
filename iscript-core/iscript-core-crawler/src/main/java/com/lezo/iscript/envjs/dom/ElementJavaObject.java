package com.lezo.iscript.envjs.dom;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class ElementJavaObject extends NativeJavaObject implements EventTarget {
	private static final long serialVersionUID = 709148757230893792L;

	public ElementJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
		super(scope, javaObject, staticType);
	}

	@Override
	public Object get(int index, Scriptable start) {
		Object result = null;
		if (has(index, start)) {
			result = super.get(index, start);
		} else if (javaObject instanceof NodeList) {
			NodeList nodeList = (NodeList) javaObject;
			result = nodeList.item(index);
		}
		return doReturn(result);
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		super.put(index, start, value);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		if (javaObject instanceof Element) {
			Element element = (Element) javaObject;
			element.setAttribute(name, value == null ? null : value.toString());
		} else if (javaObject instanceof Node) {
		}
		super.put(name, start, value);
	}

	@Override
	public Object get(String name, Scriptable start) {
		Object result = null;
		if (has(name, start)) {
			result = super.get(name, start);
		} else if (javaObject instanceof Element) {
			Element element = (Element) javaObject;
			result = element.getAttribute(name);
		} else if (javaObject instanceof Node) {
			Node node = (Node) javaObject;
			if (node.hasAttributes()) {
				node = node.getAttributes().getNamedItem(name);
				result = node != null ? node.getNodeValue() : result;
			}
		}
		return doReturn(result);
	}

	private Object doReturn(Object result) {
		return result;
	}

	@Override
	public void addEventListener(String type, EventListener listener, boolean useCapture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEventListener(String type, EventListener listener, boolean useCapture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dispatchEvent(Event evt) throws EventException {
		// TODO Auto-generated method stub
		return false;
	}
}
