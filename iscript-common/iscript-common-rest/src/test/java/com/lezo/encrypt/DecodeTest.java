package com.lezo.encrypt;

import java.nio.charset.Charset;

import org.apache.commons.codec.Charsets;
import org.junit.Test;

public class DecodeTest {

	@Test
	public void test() {
		String value = "60CDC52E093A40DF261070A7B491241430713222ECA16B97C2005E0CB490484AB822472F34F086C912815685A4D1854F185E1F8218310D557C606108E550CCCE186700AB5D83339336E801279512F990DE7C020189B1E70F38037707A872A00614731EABF8102ACB284270032071ED0FB0607705C470ED42A85366A6E8102203BC72770C8450C9C22250742500C08F183CB83A050CB020D1820716279C208E110811410CC052634282405C84F4D04A81BC61678700F0CC460E532BABBC20E390AC22730ED0F0C2DE3A013B0128B006040461780C48702A9F3E446F2FB0504197B4C14B0F49D01B1DCAEF2028DDC2BF82";
		byte[] bytes = hexString2Bytes(value, false);
		System.out.println(new String(bytes,Charsets.US_ASCII));
	}

	private static String getHexSeed(boolean isLowerCase) {
		String seed = null;
		if (isLowerCase) {
			seed = "0123456789abcdef";
		} else {
			seed = "0123456789ABCDEF";
		}

		return seed;
	}

	public static String byte2Hex(byte b, boolean isLowerCase) {
		String seed = getHexSeed(isLowerCase);
		return "" + seed.charAt(0xf & b >> 4) + seed.charAt(0xf & b);
	}

	public static byte hex2Byte(String str, boolean isLowerCase) {
		String seed = getHexSeed(isLowerCase);
		return (byte) (seed.indexOf(str.substring(0, 1)) * 16 + seed.indexOf(str.substring(1, 2)));
	}

	public static String bytes2HexString(byte[] bytes, boolean isLowerCase) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			result += byte2Hex(bytes[i], isLowerCase);
		}
		return result;
	}

	public static byte[] hexString2Bytes(String str, boolean isLowerCase) {
		byte[] b = new byte[str.length() / 2];
		for (int i = 0; i < b.length; i++) {
			String s = str.substring(i * 2, i * 2 + 2);
			b[i] = hex2Byte(s, isLowerCase);
		}
		return b;
	}
}
