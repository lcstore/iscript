package com.lezo.iscript.resulter.similar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.lezo.iscript.resulter.token.CharPart;
import com.lezo.iscript.resulter.token.CharPartUtils;
import com.lezo.iscript.resulter.token.CharTokenizer;

public class CharTokenizerTest {

	@Test
	public void testToken() throws Exception {
		List<String> sourceList = FileUtils.readLines(new File("src/test/resources/data/token.txt"), "UTF-8");
		CharTokenizer tokenizer = new CharTokenizer();
		List<CharPart> parts = new ArrayList<CharPart>();
		for (String str : sourceList) {
			CharPart charPart = new CharPart();
			parts.add(charPart);
			charPart.setToken(str);
			charPart.setTokenizer(tokenizer);
			// break;
		}
		List<CharPart> charList = tokenizer.doToken(parts);
		List<CharPart> srcList = new ArrayList<CharPart>();
		srcList.add(null);
		for (CharPart charPart : charList) {
			srcList.set(0, charPart);
			List<CharPart> destList = new ArrayList<CharPart>();
			CharPartUtils.findLastLevelCharParts(srcList, destList);
			System.out.println(charPart.getToken() + ":" + destList);
		}
	}
}
