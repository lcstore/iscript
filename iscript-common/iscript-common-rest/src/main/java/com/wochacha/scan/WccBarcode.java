package com.wochacha.scan;

import java.util.StringTokenizer;

public class WccBarcode {
	private static int initResult = 1;
	static {
		try {
			String key = "java.library.path";
			String libPath = System.getProperty(key);
//			libPath += ":" + "/home/apps/testProxy";
//			System.setProperty(key, libPath);
			System.out.println("libPath:" + libPath);
			System.loadLibrary("gcbarcode_0");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static native String conv(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

	public static native byte[] deRes(byte[] paramArrayOfByte, int paramInt);

	public static native byte[] enReq(byte[] paramArrayOfByte, int paramInt);

	public native int wccInit() throws Exception;

	public static void main(String[] args) throws Exception {
		WccBarcode wccBarcode = new WccBarcode();
		// initResult = wccBarcode.wccInit();
		String key = "java.library.path";
		String libPath = System.getProperty(key);
		StringTokenizer tokenizer = new StringTokenizer(libPath);
		while (tokenizer.hasMoreTokens()) {
			System.out.println(tokenizer.nextToken());
		}

	}
}
