package com.lezp.iscript.envjs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

public class UaDecoder {

	@Test
	public void test() throws Exception {
		String src = "if (xkvi[q2('OfddEaOentvListLXnereO', 4, 1)]) {";
		src = "if (!xkvi[ib('Wlocdgmeutnn', 3, 1)][m1('LxniuoueqDIC', 3, 1)] && xkvi[m1('LhocdVmeutnl', 3, 1)][ib('llxpeRndaow', 3, 1)]) {";
		Pattern oReg = Pattern.compile("((m1)|(q2)|(xm)|(ib))\\(['0-9a-zA-Z]+,[\\s]*[0-9]+,[\\s]*[0-9]+\\)");
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();
		Reader in = new FileReader(new File("src/test/resources/taotao/ua/fn.decoder.js"));
		cx.evaluateReader(scope, in, "decoder", 0, null);
		IOUtils.closeQuietly(in);
		List<String> srcList = FileUtils.readLines(new File("src/main/resources/envjs/jsDocument.js"));
		String pathname = "src/test/resources/taotao/ua/ua.decode.js." + System.currentTimeMillis();
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathname)));
		for (String line : srcList) {
			Matcher matcher = oReg.matcher(line);
			while (matcher.find()) {
				String encode = matcher.group();
				String source = "var dest = " + encode + ";";
				cx.evaluateString(scope, source, "decode", 0, null);
				Object destObject = ScriptableObject.getProperty(scope, "dest");
				String dest = Context.toString(destObject);
				line = line.replace(encode, "'" + dest + "'");
			}
			System.out.println(line);
			bw.append(line);
			bw.append("\n");
		}
		bw.flush();
		IOUtils.closeQuietly(bw);
	}
}
