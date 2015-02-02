package com.lezo.encrypt;

import java.io.ByteArrayOutputStream;

public class AsciiHex {
	public static void main(String[] args) throws Exception {
		System.out.println(encode("barcode"));
		System.out.println(decode(encode("中文ab123")));
		String string = decode("00000099ACED00057372010022636F6D2E6C657A6F2E697363726970742E7965616D2E696F2E496F526571756573747870707400687B227473697A65223A302C22746D6178223A302C2270726F78796572726F7273223A5B5D2C2274616374697665223A302C22637374616D70223A302C226E616D65223A22653230303140353235343030613263316439222C2270726F7879616374697665223A307D");
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
