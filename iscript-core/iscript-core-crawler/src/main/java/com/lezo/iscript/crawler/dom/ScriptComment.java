package com.lezo.iscript.crawler.dom;

import org.w3c.dom.Comment;
import org.w3c.dom.Node;

public class ScriptComment extends ScriptText implements Comment {

	public ScriptComment(String text, String baseUri) {
		super(text, baseUri);
	}

	@Override
	public String getNodeName() {
		return "#comment";
	}

	@Override
	public short getNodeType() {
		return Node.COMMENT_NODE;
	}

}
