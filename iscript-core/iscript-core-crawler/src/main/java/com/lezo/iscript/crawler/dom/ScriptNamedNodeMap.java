package com.lezo.iscript.crawler.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ScriptNamedNodeMap implements NamedNodeMap {
	private List<Node> attributeList;
	private Map<String, Node> attributeMap;

	public ScriptNamedNodeMap(Map<String, String> attrMap) {
		super();
		if (attrMap != null) {
			this.attributeList = new ArrayList<Node>(attrMap.size());
			this.attributeMap = new HashMap<String, Node>();
			for (Entry<String, String> entry : attrMap.entrySet()) {
				Attr attr = new ScriptAttr(entry.getKey(), entry.getValue());
				attributeMap.put(attr.getNodeName(), attr);
			}
		}
	}

	public ScriptNamedNodeMap(List<Node> attrList) {
		super();
		if (attrList == null) {
			return;
		}
		this.attributeList = attrList;
		this.attributeMap = new HashMap<String, Node>();
		for (Node attr : attrList) {
			attributeMap.put(attr.getNodeName(), attr);
		}
	}

	@Override
	public Node getNamedItem(String name) {
		return this.attributeMap.get(name);
	}

	@Override
	public Node setNamedItem(Node arg) throws DOMException {
		Object prevValue = this.attributeMap.put(arg.getNodeName(), arg);
		if (prevValue != null) {
			this.attributeList.remove(prevValue);
		}
		this.attributeList.add(arg);
		return arg;
	}

	@Override
	public Node removeNamedItem(String name) throws DOMException {
		return this.attributeMap.remove(name);
	}

	@Override
	public Node item(int index) {
		return this.attributeList.get(index);
	}

	@Override
	public int getLength() {
		return this.attributeList == null ? 0 : this.attributeList.size();
	}

	@Override
	public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "No namespace support");
	}

	@Override
	public Node setNamedItemNS(Node arg) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "No namespace support");
	}

	@Override
	public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "No namespace support");
	}

}
