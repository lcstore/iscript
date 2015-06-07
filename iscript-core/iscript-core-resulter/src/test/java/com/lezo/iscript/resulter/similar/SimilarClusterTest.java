package com.lezo.iscript.resulter.similar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.lezo.iscript.resulter.token.CharPart;
import com.lezo.iscript.resulter.token.CharTokenizer;

public class SimilarClusterTest {

	@Test
	public void testCluster() throws IOException {
		List<String> sourceList = FileUtils.readLines(new File("src/test/resources/data/similar.txt"), "UTF-8");
		CharTokenizer tokenizer = new CharTokenizer();
		List<CharPart> parts = new ArrayList<CharPart>();
		for (String str : sourceList) {
			CharPart charPart = new CharPart();
			parts.add(charPart);
			charPart.setToken(str);
			charPart.setTokenizer(tokenizer);
		}
		tokenizer.doToken(parts);

//		SimilarKmeansClusterer kmeans = new SimilarKmeansClusterer(3);
//		List<CharPartPoint> srcList = new ArrayList<CharPartPoint>();
//		for (CharPart cPart : parts) {
//			srcList.add(new CharPartPoint(cPart));
//		}
//		List<CentroidCluster<CharPartPoint>> clusters = kmeans.cluster(srcList);
//		for (CentroidCluster<CharPartPoint> cl : clusters) {
//			System.err.println("center:" + cl.getCenter());
//			int hasCode = cl.getCenter().hashCode();
//			for (Clusterable pt : cl.getPoints()) {
//				System.err.println(hasCode + ":" + pt);
//			}
//		}
	}

}
