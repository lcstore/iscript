package com.lezo.iscript.envjs.dom;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ElementJavaObject extends NativeJavaObject {
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
			result = result == null ? Undefined.instance : result;
		}
		return doReturn(result, start);
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		super.put(index, start, value);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		if (javaObject instanceof Element) {
			Element element = (Element) javaObject;
			if ("innerHTML".equals(name)) {
				setInnerHTML(element, value.toString());
			} else {
				if ("id".equals(name)) {
					DocumentAdapt document = findDocument(start);
					document.setElementById(value.toString(), element);
				}
				element.setAttribute(name, value == null ? null : value.toString());
			}
		} else if (prototype != null) {
			prototype.put(name, start, value);
		} else {
			super.put(name, start, value);
		}
	}

	private DocumentAdapt findDocument(Scriptable start) {
		Scriptable actScriptable = start;
		Object documentObject = null;
		while (actScriptable != null) {
			documentObject = ScriptableObject.getProperty(actScriptable, "document");
			if (documentObject != null && documentObject != ScriptableObject.NOT_FOUND
					&& documentObject instanceof DocumentAdapt) {
				break;
			}
			actScriptable = actScriptable.getParentScope();
		}
		DocumentAdapt documentAdapt = (DocumentAdapt) Context.jsToJava(documentObject, DocumentAdapt.class);
		return documentAdapt;
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
		return doReturn(result, start);
	}

	private Object doReturn(Object result, Scriptable scope) {
		if (result != null) {
			return result;
		}
		return Scriptable.NOT_FOUND;
	}

	public void setInnerHTML(Element element, String html) {
		SAXReader saxReader = new SAXReader();
		try {
			html = "<XmlRoot>" + html + "</XmlRoot>";
			InputStream in = new ByteArrayInputStream(html.getBytes());
			org.dom4j.Document document = saxReader.read(in);
			org.dom4j.Element root = document.getRootElement();
			Iterator<?> it = root.elementIterator();
			while (it.hasNext()) {
				org.dom4j.Element xmlElement = (org.dom4j.Element) it.next();
				addElement(element, xmlElement);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private void addElement(Element element, org.dom4j.Element xmlElement) {
		String tagName = xmlElement.getName();
		org.w3c.dom.Element childElement = element.getOwnerDocument().createElement(tagName);
		element.appendChild(childElement);
		if (xmlElement.hasContent()) {
//			childElement.setTextContent(xmlElement.getTextTrim());
			childElement.setNodeValue(xmlElement.getTextTrim());
		}
		childElement.setNodeValue(xmlElement.getStringValue());
		Iterator<?> ait = xmlElement.attributeIterator();
		while (ait.hasNext()) {
			org.dom4j.Attribute attr = (org.dom4j.Attribute) ait.next();
			childElement.setAttribute(attr.getName(), attr.getValue());
		}
		Iterator<?> it = xmlElement.elementIterator();
		while (it.hasNext()) {
			org.dom4j.Element xmlChildElement = (org.dom4j.Element) it.next();
			addElement(childElement, xmlChildElement);
		}

	}
}
