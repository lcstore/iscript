package com.lezo.iscript.resulter.similar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.lezo.iscript.resulter.token.CharPart;
import com.lezo.iscript.resulter.token.CharPartUtils;
import com.lezo.iscript.resulter.token.CharTokenizer;

public class SimilarCalculatorTest {

	@Test
	public void testSimilar() throws Exception {
		List<String> sourceList = FileUtils.readLines(new File("src/test/resources/data/similar.txt"), "UTF-8");
		CharTokenizer tokenizer = new CharTokenizer();
		List<CharPart> parts = new ArrayList<CharPart>();
		for (String str : sourceList) {
			CharPart charPart = new CharPart();
			parts.add(charPart);
			charPart.setToken(str);
			charPart.setTokenizer(tokenizer);
			// break;
		}
		tokenizer.doToken(parts);
		BaseSimilarCalculator baseSimilarCalculator = new BaseSimilarCalculator();
		int index = 0;
		Float score = baseSimilarCalculator.calcSimilar(parts.get(index), parts.get(index + 1));
		List<CharPart> srcList = new ArrayList<CharPart>();
		srcList.add(null);
		CharPart srcPart = parts.get(index);
		srcList.set(0, srcPart);
		List<CharPart> destList = new ArrayList<CharPart>();
		CharPartUtils.findLastLevelCharParts(srcList, destList);
		System.out.println(srcPart.getToken() + ":" + destList);
		srcPart = parts.get(index+1);
		srcList.set(0, srcPart);
		destList = new ArrayList<CharPart>();
		CharPartUtils.findLastLevelCharParts(srcList, destList);
		System.out.println(srcPart.getToken() + ":" + destList);
		System.err.println(score);
	}
}
