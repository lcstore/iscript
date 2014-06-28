package com.lezo.encrypt;

import java.io.ByteArrayOutputStream;

public class AsciiHex {
	public static void main(String[] args) throws Exception {
		System.out.println(encode("barcode"));
		System.out.println(decode(encode("中文ab123")));
		String string = decode("7875D4AFB99BC8CF261070A7B491241430713222ECA16B97C2005E0CB490484AB822472F34F086C9825A59240040C19D9A521DA15C90AEC8FA426582F453B94412F85C0BBD40175F6294638785D0C88A1CC70408244353865CDB47AB9533ADCB08221C03FC802D902451750164B1C9840A22588B982023D33C0255A2A451C18E04720C226C20695584500A22EC800FD37CF33306E010404086120E218C8027152470468D647100478651280238202C990E61522420D060D33030662B14408E97E8730400E590E9C386500E2938D057020C293A042811215586410820D8A10DDFA47167070471C9868A4356077CD04753B66261078450E845368367A634500C15A46335297DC0A3D7CAEF2028DDC2BF82");
		System.out.println(string);
		System.out.println(new String(string.getBytes(), "US-ASCII"));
	}

	/*
	 * 16进制数字字符集
	 */
	private static String hexString = "0123456789ABCDEF";

	/*
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/*
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}
}
