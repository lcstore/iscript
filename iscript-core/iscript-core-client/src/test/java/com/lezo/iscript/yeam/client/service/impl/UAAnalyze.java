package com.lezo.iscript.yeam.client.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;

public class UAAnalyze {
	@Test
	public void testRun() throws IOException, Exception {
		String source = "src/test/resources/js/ua.js";
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();

		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		ScriptableObject.putProperty(scope, "$document", doc);

		Reader hostReader = new FileReader(new File("src/test/resources/js/inithost.js"));
		cx.evaluateReader(scope, hostReader, "cmd", 0, null);

		Reader in = new FileReader(new File(source));
		cx.evaluateReader(scope, in, "cmd", 0, null);

		source = "var kop=2;var ohwp = [];ohwp.push(20);var osub=[];osub.push('to');var otime=[];otime.push(955993);otime.push(1397642042902);otime.push('20');osub.push(otime);var cl5m = ro(ohwp, kop);";
		source = "var op = [];op.push(20);var osub=[];osub.push('to');var otime=[];otime.push(955993);otime.push(1397642042902);otime.push('20');osub.push(otime);op.push(osub);var cl5m = q3t(op)";
		source = "UA_Opt.Token=new Date().getTime()+':'+Math.random();UA_Opt.reload()";
		cx.evaluateString(scope, source, "cmd", 0, null);
		// TimeUnit.SECONDS.sleep(10);
		// cx.evaluateString(scope, source, "cmd", 0, null);
		Object rs = ScriptableObject.getProperty(scope, "ua");
		System.out.println(Context.toString(rs));
		IOUtils.closeQuietly(hostReader);
		IOUtils.closeQuietly(in);
	}

	@Test
	public void decodeUA() throws IOException {
		String source = "src/test/resources/js/ua_action_log.js";
		String encoding = "UTF-8";
		List<String> lines = FileUtils.readLines(new File(source), encoding);
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();
		String defnSource = "src/test/resources/js/defunction.js";
		Reader in = new FileReader(new File(defnSource));
		cx.evaluateReader(scope, in, "cmd", 0, null);
		Pattern deReg = Pattern.compile("[0-9a-zA-Z]+\\('[a-zA-Z]+',[0-9\\s]+,[0-9\\s]+\\)");
		// deReg = Pattern.compile("\\(.*?,.*?,.*?\\)");
		String deUAOpt = "src/test/resources/js/ua_action_log." + System.currentTimeMillis() + ".js";
		Writer out = new FileWriter(new File(deUAOpt));
		BufferedWriter bWriter = new BufferedWriter(out);
		for (String line : lines) {
			Matcher matcher = deReg.matcher(line);
			while (matcher.find()) {
				String callFn = matcher.group();
				Object result = cx.evaluateString(scope, callFn, "cmd", 0, null);
				String callBack = Context.toString(result);
				line = line.replace(callFn, "'" + callBack + "'");
			}
			System.out.println(line);
			bWriter.append(line);
			bWriter.append("\n");
		}
		bWriter.flush();
		bWriter.close();
		IOUtils.closeQuietly(in);
	}
}
