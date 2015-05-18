package com.lezo.iscript.crawler.dom;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;

public class ScriptCDATASection extends ScriptText implements CDATASection {

	public ScriptCDATASection(String text, String baseUri) {
		super(text, baseUri);
	}

	@Override
	public String getNodeName() {
		return "#cdata-section";
	}

	@Override
	public short getNodeType() {
		return Node.CDATA_SECTION_NODE;
	}

}
