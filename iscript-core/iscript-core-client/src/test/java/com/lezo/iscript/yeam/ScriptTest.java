package com.lezo.iscript.yeam;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.crawler.script.CommonContext;
import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.envjs.EnvjsUtils;
import com.lezo.iscript.envjs.dom.DocumentAdapt;
import com.sun.jndi.toolkit.ctx.Continuation;

public class ScriptTest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void test() throws Exception {
		Context cx = EnvjsUtils.enterContext();
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
		String source = FileUtils.readFileToString(new File("src/test/resources/jQuery.js"));
		cx.evaluateString(scope, source, "cmd", 0, null);
		String html = null;
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		String url = "http://www.yhd.com/";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", url);
		// String html = HttpClientUtils.getContent(client, get);
		// FileUtils.writeStringToFile(new
		// File("src/test/resources/homePage.js"), html, "UTF-8");
		html = FileUtils.readFileToString(new File("src/test/resources/homePage.js"));
		Document dom = Jsoup.parse(html, url);
		Elements scriptAs = dom.select("script");
		for (Element ele : scriptAs) {
			if ("loucengTagContent".equals(ele.id())) {
				continue;
			}
			String script = ele.html();
			if (script.indexOf("URLPrefix") > 0 || script.indexOf("_gaq") > 0) {
				cx.evaluateString(scope, ele.html(), "cmd", 0, null);
			}
		}
		get = new HttpGet("http://www.google-analytics.com/ga.js");
		html = HttpClientUtils.getContent(client, get);
		html += "ilog('ga.cookie:'+document.cookie)";
		cx.evaluateString(scope, html, "ga", 0, null);

		html = "document.charset='UTF-8'";
		html += FileUtils.readFileToString(new File("src/test/resources/bcore.js"));
		html += "ilog('bcore.cookie:'+document.cookie)";
		cx.evaluateString(scope, html, "bcore", 0, null);

		source = FileUtils.readFileToString(new File("src/test/resources/global.js"));
		cx.evaluateString(scope, source, "global", 0, null);

		//
		for (Element ele : scriptAs) {
			if ("loucengTagContent".equals(ele.id())) {
				continue;
			}
			String script = ele.html();
			if (script.indexOf("URLPrefix") > 0 || script.indexOf("_gaq") > 0) {
				continue;
			}
			cx.evaluateString(scope, ele.html(), "cmd", 0, null);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("document.body=document.getElementsByTagName('body')[0];");
		sb.append("var event = document.createEvent('HTMLEvents');");
		sb.append("event.initEvent('DOMContentLoaded', false, false);");
		sb.append("document.dispatchEvent(event);");
		// cx.evaluateString(scope, sb.toString(), "event", 0, null);
		html = "ilog('end.cookie:'+trackerContainer.toUrl());";
		html += "ilog('end.cookie:'+document.cookie);";
		cx.evaluateString(scope, html, "cmd", 0, null);
		System.out.println("end script......");
	}

	@Test
	public void testParser() throws Exception {
		Context cx = EnvjsUtils.enterContext();
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
		Object documentObject = ScriptableObject.getProperty(scope, "document");
		DocumentAdapt documentAdapt = (DocumentAdapt) Context.jsToJava(documentObject, DocumentAdapt.class);
		org.w3c.dom.Element divElement = documentAdapt.createElement("div");
		String html = "<link/><table></table><a href='/a' style='color:red;float:left;opacity:.55;'>a</a><input type='checkbox'/>";
		html = "<root>" + html + "</root>";
		SAXReader saxReader = new SAXReader();
		InputStream in = new ByteArrayInputStream(html.getBytes());
		org.dom4j.Document document = saxReader.read(in);
		org.dom4j.Element root = document.getRootElement();
		Iterator it = root.elementIterator();
		while (it.hasNext()) {
			org.dom4j.Element element = (org.dom4j.Element) it.next();
			String tagName = element.getName();
			org.w3c.dom.Element newElement = divElement.getOwnerDocument().createElement(tagName);
			divElement.appendChild(newElement);
			Iterator ait = element.attributeIterator();
			while (ait.hasNext()) {
				org.dom4j.Attribute attr = (org.dom4j.Attribute) ait.next();
				newElement.setAttribute(attr.getName(), attr.getValue());
			}
			System.out.println(element.getName());
		}
		System.out.println(divElement.hasChildNodes());
	}

	@Test
	public void testBaiduQa() throws Exception {
		Context cx = EnvjsUtils.enterContext();
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
		StringBuilder sb = new StringBuilder();
		sb.append("function Image(width, height){ilog('new Image..');_globalImg=this;};");
		sb.append("Image.prototype = document.createElement('img');");

		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		HttpGet get = new HttpGet("http://www.google-analytics.com/ga.js");
		get.addHeader("Referer", "http://www.yhd.com/");
		String jsCookieHtml = FileUtils.readFileToString(new File("src/test/resources/yhd.qa.js"), "UTF-8");
		sb.append(jsCookieHtml);
		sb.append("ilog('document.cookie:'+document.cookie)");
		// sb.append("var logoUrl=_globalImg.src;");
		cx.evaluateString(scope, sb.toString(), "ga.cookie", 0, null);
	}

	@Test
	public void testUa() throws Exception {
		int optimizationLevel = -1;
		Context cx = EnvjsUtils.enterContext();
		cx.setOptimizationLevel(optimizationLevel);
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
		StringBuilder sb = new StringBuilder();
		sb.append("window.ua = '';");
		sb.append("window.UA_Opt = {LogVal: \"ua\",MaxMCLog: 5, MaxMPLog: 5,MaxKSLog: 5,Token: (new Date).getTime() + \":\" + Math.random(),SendMethod: 8,Flag: 14222}");
		cx.evaluateString(scope, sb.toString(), "tb.opt", 0, null);
		String code = FileUtils.readFileToString(new File("src/test/resources/js/deua.js"));
		code += "window.UA_Opt.reload();var newUa = window.ua;";
		cx.evaluateString(scope, code, "tb.ua", 0, null);
		Object uaObject = ScriptableObject.getProperty(scope, "newUa");
		String ua = Context.toString(uaObject);
		System.out.println("ua:" + ua);
		System.out.println("end.......");
	}

	@Test
	public void testUa2() throws Exception {
		int optimizationLevel = -2;
		Context cx = EnvjsUtils.enterContext();
		EvaluatorException er = Context.reportRuntimeError("...");
		// er.getScriptStackTrace().length()
		cx.setOptimizationLevel(optimizationLevel);
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
		String argsString = FileUtils.readFileToString(new File("src/test/resources/js/uaargs.js"));
		cx.evaluateString(scope, argsString, "tb.ua.args", 0, null);
		String code = FileUtils.readFileToString(new File("src/test/resources/js/deua.1402674671174.js"));
		cx.evaluateString(scope, code, "tb.deua", 0, null);
		String execString = FileUtils.readFileToString(new File("src/test/resources/js/uaexec.js"));
		cx.evaluateString(scope, execString, "tb.uaexec", 0, null);
		System.out.println("end.......");
	}
	@Test
	public void testUaAuth() throws Exception {
		int optimizationLevel = -2;
		Context cx = EnvjsUtils.enterContext();
		EvaluatorException er = Context.reportRuntimeError("...");
		// er.getScriptStackTrace().length()
		cx.setOptimizationLevel(optimizationLevel);
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
		String argsString = FileUtils.readFileToString(new File("src/test/resources/js/uaargs.js"));
		cx.evaluateString(scope, argsString, "tb.ua.args", 0, null);
		String code = FileUtils.readFileToString(new File("src/test/resources/js/ua_authcenter_login.1402822683653.js"));
		cx.evaluateString(scope, code, "tb.deua", 0, null);
		String execString = FileUtils.readFileToString(new File("src/test/resources/js/uaexec.js"));
		cx.evaluateString(scope, execString, "tb.uaexec", 0, null);
		System.out.println("end.......");
	}
	@Test
	public void testUaLog() throws Exception {
		Context cx = EnvjsUtils.enterContext();
		// er.getScriptStackTrace().length()
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
		String argsString = FileUtils.readFileToString(new File("src/test/resources/js/uaargs.js"));
		cx.evaluateString(scope, argsString, "tb.ua.args", 0, null);
		String code = FileUtils.readFileToString(new File("src/test/resources/js/ua_action_log.1402939313185.js"));
		cx.evaluateString(scope, code, "tb.deua", 0, null);
		String execString = FileUtils.readFileToString(new File("src/test/resources/js/uaexec.js"));
		cx.evaluateString(scope, execString, "tb.uaexec", 0, null);
		System.out.println("end.......");
	}
	@Test
	public void testUm() throws Exception {
		Context cx = EnvjsUtils.enterContext();
		// er.getScriptStackTrace().length()
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
		String code = FileUtils.readFileToString(new File("src/test/resources/js/um.js"));
		cx.evaluateString(scope, code, "tb.um", 0, null);
		System.out.println("end.......");
	}

	@Test
	public void testApiCall() throws Exception {
		Context cx = EnvjsUtils.enterContext();
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
		String argsString = FileUtils.readFileToString(new File("src/test/resources/js/uaargs.js"));
		cx.evaluateString(scope, argsString, "tb.ua.args", 0, null);
		String code = FileUtils.readFileToString(new File("src/test/resources/js/deua.1402674671174.js"));
		cx.evaluateString(scope, code, "tb.deua", 0, null);
		String execString = FileUtils.readFileToString(new File("src/test/resources/js/uaexec.js"));
		int index = 0;
		int max = 500;
		while (index < max) {
			try {
				TimeUnit.SECONDS.sleep(10);
				cx.evaluateString(scope, execString, "tb.uaexec", 0, null);
				Object uaObject = ScriptableObject.getProperty(scope, "newUa");
				String ua = Context.toString(uaObject);

				String apiUrl = "http://api.taobao.com/apitools/getResult.htm";
				HttpPost post = new HttpPost(apiUrl);
				post.addHeader("Referer",
						"http://api.taobao.com/apitools/apiTools.htm?catId=4&apiName=taobao.item.skus.get&scopeId=");
				post.addHeader("Accept-Encoding", "gzip, deflate");
				String pCode = "39071575593";
				List<NameValuePair> parameters = new ArrayList<NameValuePair>();
				parameters.add(new BasicNameValuePair("api_soure", "0"));
				parameters.add(new BasicNameValuePair("app_key", "系统分配"));
				parameters.add(new BasicNameValuePair("app_secret", "系统分配"));
				parameters.add(new BasicNameValuePair("codeType", "JAVA"));
				parameters.add(new BasicNameValuePair("fields",
						"sku_id,num_iid,properties,quantity,price,outer_id,created,modified"));
				parameters.add(new BasicNameValuePair("format", "json"));
				parameters.add(new BasicNameValuePair("method", "taobao.item.skus.get"));
				parameters.add(new BasicNameValuePair("num_iids", pCode));
				parameters.add(new BasicNameValuePair("restId", "2"));
				parameters.add(new BasicNameValuePair("session", ""));
				parameters.add(new BasicNameValuePair("sip_http_method", "POST"));
				parameters.add(new BasicNameValuePair("ua", ua));
				HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");
				post.setEntity(entity);

				DefaultHttpClient client = HttpClientUtils.createHttpClient();
				String html = HttpClientUtils.getContent(client, post);
				index++;
				logger.info(index + ":" + html);
			} catch (Exception e) {
				logger.warn("" + index, e);
			}
		}

	}

	@Test
	public void testArgs() throws Exception {
		Context cx = EnvjsUtils.enterContext();
		cx.setOptimizationLevel(-2);
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);
		String code = FileUtils.readFileToString(new File("src/test/resources/js/args.js"));
		cx.evaluateString(scope, code, "tb.opt", 0, null);
		System.out.println("end......." + cx.isGeneratingSource());
	}
}
