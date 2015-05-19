package com.lezo.iscript.crawler.dom;

import java.util.List;

import org.jsoup.nodes.Node;

import com.lezo.iscript.crawler.dom.env.ScriptWindow;

public class ScriptHtmlParser {

	public static ScriptDocument parser(org.jsoup.nodes.Document document) {
		return parser(new ScriptWindow(), document);
	}

	public static ScriptDocument parser(ScriptWindow window, org.jsoup.nodes.Document document) {
		ScriptDocument scriptDocument = new ScriptDocument(window, document);
		List<Node> childList = document.childNodes();
		for (Node child : childList) {
			doCopy(child, scriptDocument, scriptDocument);
		}
		return scriptDocument;
	}

	public static void doCopy(Node source, ScriptElement parent, final ScriptDocument scriptDocument) {
		ScriptElement newChild = scriptDocument.createElementByNode(source);
		parent.appendChild(newChild);
		List<Node> childList = source.childNodes();
		for (Node child : childList) {
			doCopy(child, newChild, scriptDocument);
		}
	}
}
