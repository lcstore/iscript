package com.lezo.iscript.crawler.dom;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ScriptNodeList implements NodeList {
	private List<Node> nodeList;

	public ScriptNodeList(List<Node> nodeList) {
		super();
		this.nodeList = nodeList;
	}

	@Override
	public Node item(int index) {
		return index < 0 || index >= getLength() ? null : nodeList.get(index);
	}

	@Override
	public int getLength() {
		return this.nodeList == null ? 0 : this.nodeList.size();
	}

}
