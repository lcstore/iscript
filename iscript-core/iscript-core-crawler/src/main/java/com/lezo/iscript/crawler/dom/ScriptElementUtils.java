package com.lezo.iscript.crawler.dom;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ScriptElementUtils {

	public static ScriptElement toElement(Node srcNode, Node parent) {
		ScriptElement sElement = (ScriptElement) parent.getOwnerDocument().createElement(srcNode.getNodeName());
		sElement.setNodeValue(srcNode.getNodeValue());
		NamedNodeMap attrs = srcNode.getAttributes();
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				Node node = attrs.item(i);
				Attr newAttr = new ScriptAttr(node.getNodeName(), node.getNodeValue());
				sElement.setAttributeNode(newAttr);
			}
		}
		sElement.setNodeType(srcNode.getNodeType());
		// srcNode.getOwnerDocument()
		NodeList nodeList = srcNode.getChildNodes();
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node newChild = nodeList.item(i);
				sElement.appendChild(newChild);
			}
		}
		return sElement;
	}

	public static ScriptElement toElement(Node newChild) {
		if (newChild == null) {
			return null;
		}
		Map<Node, ScriptElement> linkMap = new HashMap<Node, ScriptElement>();
		Node srcChild = newChild;
		while (srcChild != null) {
			linkMap.put(srcChild, toElement(srcChild, null));
			srcChild = srcChild.getParentNode();
		}
		srcChild = newChild;
		while (srcChild != null) {
			ScriptElement curElement = linkMap.get(srcChild);
			if (srcChild.getParentNode() != null) {
				ScriptElement parentElement = linkMap.get(srcChild.getParentNode());
				curElement.setParent(parentElement);
			}
			srcChild = srcChild.getParentNode();
		}
		return linkMap.get(newChild);
	}

}
