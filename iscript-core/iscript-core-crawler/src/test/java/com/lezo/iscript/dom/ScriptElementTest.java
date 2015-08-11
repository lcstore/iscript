package com.lezo.iscript.dom;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lezo.iscript.crawler.dom.ScriptDocument;
import com.lezo.iscript.crawler.dom.ScriptHtmlParser;
import com.lezo.iscript.crawler.dom.browser.ScriptWindow;

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
		Document doc = Jsoup.parse(source, "http://localhost.com");
		ScriptWindow window = new ScriptWindow();
		ScriptDocument scriptDocument = ScriptHtmlParser.parser(doc);
		window.setDocument(scriptDocument);
		System.err.println("before:" + doc);
		Element scriptEle = doc.select("script").first();
		System.err.println("script:" + scriptEle.html());
		window.eval(scriptEle.html());
		System.err.println("after:" + doc);
	}

	@Test
	public void testDictScript() throws Exception {
		String source = FileUtils.readFileToString(new File("src/test/resources/data/dict.cn.html"), "UTF-8");
		Document doc = Jsoup.parse(source);
		ScriptDocument scriptDocument = ScriptHtmlParser.parser(doc);
		Elements scriptEls = doc.select("script");
		ScriptWindow window = new ScriptWindow();
		window.setDocument(scriptDocument);
		int index = 0;
		for (Element ele : scriptEls) {
			String script = ele.html();
			if (StringUtils.isBlank(script)) {
				continue;
			}
			index++;
			System.out.println("index:" + index + ":\n" + script);
			window.eval(script);
		}
		Assert.assertEquals("http://dict.cn",
				Context.toString(ScriptableObject.getProperty(window.getScope(), "dict_homepath")));

		System.err.println("after:" + doc);
	}

	@Test
	public void testRegExpScript() throws Exception {
		String source = FileUtils.readFileToString(new File("src/test/resources/data/test.regex.html"), "UTF-8");
		Document doc = Jsoup.parse(source);
		ScriptDocument scriptDocument = ScriptHtmlParser.parser(doc);
		Elements scriptEls = doc.select("script");
		ScriptWindow window = new ScriptWindow();
		window.setDocument(scriptDocument);
		int index = 0;
		for (Element ele : scriptEls) {
			String script = ele.html();
			if (StringUtils.isBlank(script)) {
				continue;
			}
			index++;
			System.out.println("index:" + index + ":\n" + script);
			window.eval(script);
		}
		System.err.println("after:" + doc);
	}

	@Test
	public void testBrowserScript() throws Exception {
		String source = FileUtils.readFileToString(new File("src/test/resources/data/test.browser.html"), "UTF-8");
		Document doc = Jsoup.parse(source);
		ScriptDocument scriptDocument = ScriptHtmlParser.parser(doc);
		Elements scriptEls = doc.select("script");
		final ScriptWindow window = new ScriptWindow();
		window.setDocument(scriptDocument);
		int index = 0;
		for (Element ele : scriptEls) {
			String script = ele.html();
			if (StringUtils.isBlank(script)) {
				continue;
			}
			index++;
			System.out.println("index:" + index + ":\n" + script);
			window.eval(script);
		}
		long timeout = 200;
		System.err.println("wait second:" + timeout);
		TimeUnit.MILLISECONDS.sleep(timeout);
		// System.err.println("after:" + doc);
	}

	@Test
	public void testTmallScript() throws Exception {
		String url = "http://list.tmall.com/search_product.htm?brand=31840&sort=s&style=w#J_Filter";
		String source = FileUtils.readFileToString(new File("src/test/resources/data/tm.html"), "UTF-8");
		Document doc = Jsoup.parse(source, url);
		ScriptDocument scriptDocument = ScriptHtmlParser.parser(doc);
		Elements scriptEls = doc.select("f script");
		ScriptWindow window = new ScriptWindow();
		window.setDocument(scriptDocument);

		String baseScript = FileUtils.readFileToString(new File("src/test/resources/data/tm.index.js"), "UTF-8");
		// window.eval("setInterval=function(aExpression,aTimeInMs){ return window.setInterval(aExpression,aTimeInMs); };");
		window.eval(baseScript);
		int index = 0;
		for (Element ele : scriptEls) {
			String script = ele.html();
			if (StringUtils.isBlank(script)) {
				continue;
			}
			index++;
			System.out.println("index:" + index + ":\n" + script);
			window.eval(script);
		}
		while (true) {
			long timeout = 1000;

			System.err.println("wait second:" + timeout + ",ck:" + scriptDocument.getCookie());
			TimeUnit.MILLISECONDS.sleep(timeout);
		}
		// System.err.println("after:" + doc);
	}
}
