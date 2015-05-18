package com.lezo.iscript.dom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lezo.iscript.crawler.dom.ScriptDocument;
import com.lezo.iscript.crawler.dom.ScriptHtmlParser;

public class ScriptElementTest {

	@Test
	public void testScriptElement() {
		String html = "<html><head name='hname'><title>First parse</title></head>"
				+ "<body><p id='home'>Parsed HTML into a doc.</p></body></html>";
		Document doc = Jsoup.parse(html);
		// ScriptElement dom = ScriptElementUtils.toElement(doc, null);
		Elements pEls = doc.select("p");
		Element pEle = pEls.first();
		System.err.println(pEle.attributes());
		System.err.println(pEle.attr("id"));
		ScriptDocument scriptDocument = ScriptHtmlParser.parser(doc);
		NodeList snl = scriptDocument.getElementsByName("p");
		System.err.println("size:" + snl.getLength());
		Node node = snl.item(0);
		System.err.println("class:" + node.getClass().getName());
		System.err.println("class:" + node.getNodeName());
		System.err.println("class:" + node.getAttributes().item(0));
		System.err.println("class:" + node.getChildNodes().item(0).getNodeValue());
	}
}
