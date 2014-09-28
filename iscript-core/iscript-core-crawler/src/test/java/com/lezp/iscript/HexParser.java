package com.lezp.iscript;

import java.net.URLDecoder;

import org.junit.Test;

public class HexParser {

	@Test
	public void test() throws Exception {
		String shex = "image";
System.out.println(URLDecoder.decode("MTAyNzQ2MzcyMA%3D%3D", "gbk"));
	}

	private static String hexToAscii(String s) {
		  int n = s.length();
		  StringBuilder sb = new StringBuilder(n / 2);
		  byte[] bytes = new byte[100];
		  for (int i = 0; i < n; i += 2) {
		    char a = s.charAt(i);
		    char b = s.charAt(i + 1);
		    sb.append((char) ((hexToInt(a) << 4) | hexToInt(b)));
		  }
		  return sb.toString();
		}

	private static int hexToInt(char ch) {
		if ('a' <= ch && ch <= 'f') {
			return ch - 'a' + 10;
		}
		if ('A' <= ch && ch <= 'F') {
			return ch - 'A' + 10;
		}
		if ('0' <= ch && ch <= '9') {
			return ch - '0';
		}
		throw new IllegalArgumentException(String.valueOf(ch));
	}
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}
}
