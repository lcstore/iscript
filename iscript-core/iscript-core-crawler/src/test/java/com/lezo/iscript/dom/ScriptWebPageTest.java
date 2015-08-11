package com.lezo.iscript.dom;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.lezo.iscript.crawler.dom.ScriptDocument;
import com.lezo.iscript.crawler.dom.ScriptHtmlParser;
import com.lezo.iscript.crawler.dom.browser.ScriptWindow;

public class ScriptWebPageTest {

	@Test
	public void testWebScript() throws IOException {
		String encoding = "UTF-8";
		String html = FileUtils.readFileToString(new File("src/test/resources/data/unblock.html"), encoding);
		String mainjs = FileUtils.readFileToString(new File("src/test/resources/data/unblock.main.js"), encoding);
		Document doc = Jsoup.parse(html, "http://www.unblockyoutube.in/index.php");
		ScriptDocument scriptDocument = ScriptHtmlParser.parser(doc);
		ScriptWindow window = new ScriptWindow();
		window.setDocument(scriptDocument);

		window.eval("ginf={url:'http://www.unblockyoutube.in',script:'secretnom.php',target:{h:'',p:'',b:'',u:''},enc:{u:'bPXGnQIAy3aAGU1LtWtpbjVQpHwVHFnIkafwPLWdCVC8DrIWd19d8UTlse6XJhtUiwgX70YALqyfH4ZJVZMSJt3ZxYK754PdqVYoJNOl1aox5d6O0GwyXqlleWg7QTb6',e:'1',x:'',p:'1'},b:'6'}");
		window.eval("parent = window;");
		window.eval(mainjs);
		window.eval("var url='https://httpbin.org/ip';");
		window.eval("var log=function(str){ if(str){java.lang.System.out.println(str);}else{java.lang.System.out.println('Null');} };");

		// window.eval("var input=document.getElementById('input'); input.value='https://httpbin.org/ip';");
		String findFormString = "var inputForm= document.getElementsByTagName('form')[0]; updateLocation(inputForm);";
		window.eval(findFormString);
		System.out.println(scriptDocument.getLocation().getHref());
	}
}
