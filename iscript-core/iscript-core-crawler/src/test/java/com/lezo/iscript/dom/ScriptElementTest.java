package com.lezo.iscript.dom;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lezo.iscript.crawler.dom.ScriptDocument;
import com.lezo.iscript.crawler.dom.ScriptHtmlParser;
import com.lezo.iscript.crawler.dom.env.ScriptWindow;

public class ScriptElementTest {

	@Test
	public void testScriptElement() {
		String html = "<html><head name='hname'><title>First parse</title></head>"
				+ "<body><p id='home'>Parsed HTML into a doc.</p></body></html>";
		Document doc = Jsoup.parse(html);
		ScriptDocument scriptDocument = ScriptHtmlParser.parser(doc);
		NodeList snl = scriptDocument.getElementsByName("p");
		Node node = snl.item(0);
		// Assert.assertEquals("p", node.getNodeName());
		// Assert.assertEquals("home",
		// node.getAttributes().getNamedItem("id").getNodeValue());
		// Assert.assertEquals("Parsed HTML into a doc.",
		// node.getChildNodes().item(0).getNodeValue());
		Assert.assertEquals("p", scriptDocument.getElementById("home").getNodeName());
	}

	@Test
	public void testWriteScript() {
		String scriptTemp = "<html><head><script>_script_</script></head><body></body></html>";
		String script = "document.write(\"<center>Choose One from 2345 Working Proxies</center><div id='proxy_select'><center><a href='https://proxylists.me/go/randomproxy' target='_blank'><b>**Select Random** &nbsp; </b></a></center>\");";
		String source = scriptTemp.replace("_script_", script);
		System.err.println(source);
		Document doc = Jsoup.parse(source);
		ScriptDocument scriptDocument = ScriptHtmlParser.parser(doc);
		Node sn = scriptDocument.getElementsByName("script").item(0);
		Assert.assertEquals("script", sn.getNodeName());
		System.err.println("cs:" + sn.getFirstChild().getNodeValue());
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();
		ScriptableObject.putConstProperty(scope, "document", scriptDocument);
		System.err.println("before:" + doc);
		source = sn.getFirstChild().getNodeValue();
		Assert.assertEquals(true, doc.select("#proxy_select").isEmpty());
		cx.evaluateString(scope, source, "write", 0, null);
		System.err.println("after:" + doc);
		Assert.assertEquals(false, doc.select("#proxy_select").isEmpty());
		Assert.assertEquals(true, scriptDocument.getElementById("proxy_select") != null);
	}

	@Test
	public void testElementScript() throws Exception {
		String source = FileUtils.readFileToString(new File("src/test/resources/data/test.element.html"), "UTF-8");
		Document doc = Jsoup.parse(source);
		ScriptWindow window = new ScriptWindow();
		ScriptDocument scriptDocument = ScriptHtmlParser.parser(window, doc);
		window.setDocument(scriptDocument);
		System.err.println("before:" + doc);
		Element scriptEle = doc.select("script").first();
		System.err.println("script:" + scriptEle.html());
		window.eval(scriptEle.html());
		System.err.println("after:" + doc);
	}
}
