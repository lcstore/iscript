package com.lezo.iscript.utils;

import org.junit.Test;

public class SimilarUtilsTest {

	@Test
	public void test() {
		String str1 = "ab1234";
		String str2 = "abcd34";
		String str3 = "12345cdv";
		System.out.println(str1 + " vs " + str1 + " = " + SimilarUtils.getLevenshteinDistance(str1, str1));
		System.out.println(str1 + " vs " + str2 + " = " + SimilarUtils.getLevenshteinDistance(str1, str2));
		System.out.println(str1 + " vs " + str3 + " = " + SimilarUtils.getLevenshteinDistance(str1, str3));
	}
}
