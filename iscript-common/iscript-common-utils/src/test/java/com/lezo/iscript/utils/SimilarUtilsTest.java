package com.lezo.iscript.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class SimilarUtilsTest {

	@Test
	public void test() {
		String str1 = "1234abcd";
		String str2 = "abcd1234";
		String str3 = "1234o我们的世界";
		System.out.println(str1 + " vs " + str1 + " = " + StringUtils.getLevenshteinDistance(str1, str1));
		System.out.println(str1 + " vs " + str2 + " = " + StringUtils.getLevenshteinDistance(str1, str2));
		System.out.println(str1 + " vs " + str3 + " = " + StringUtils.getLevenshteinDistance(str1, str3));
		System.out.println(str3 + " vs " + str1 + " = " + StringUtils.getLevenshteinDistance(str3, str1));
	}
}
