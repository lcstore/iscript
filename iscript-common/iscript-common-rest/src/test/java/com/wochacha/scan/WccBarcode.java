package com.wochacha.scan;

import java.util.StringTokenizer;

public class WccBarcode {
	static {
		try {
			System.loadLibrary("gcbarcode_0");
		} catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
			localUnsatisfiedLinkError.printStackTrace();
		}
	}
	public static native String conv(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

	public static native byte[] deRes(byte[] paramArrayOfByte, int paramInt);

	public static native byte[] enReq(byte[] paramArrayOfByte, int paramInt);

	public static void main(String[] args) {
		WccBarcode test = new WccBarcode();
		String valueString="C85C6C348102C177261070A7B491241430713222ECA16B97C2005E0CB490484AB822472F34F086C9825A59240060E19D9A521DA15C90AEC8FA426582F453B94412F85C0BBD40175F6294638785D0C88A1CC70408244353865CDB47AB9533ADCB08221C03FC802D902451750164B1C9840A22588B982023D32CB274A31451D18E1830660084A108D716C01020B400CDD3AC336306E04090D084021E215C103707B422730D607080C78C10442094E1601EB4726606C07169059253252F359045429C72410EE4A09BD506DA162BD0A0CF01A8247A08C040615486414429E4E0689FB461670794F0C0CE2A403A2B88110F8FBC21678504B181843A1047273C90078616723780C8230B40CAEF222ADDC0BF82";
		byte[] paramArrayOfByte=valueString.getBytes();
		int paramInt=0;
		String property = System.getProperty("java.library.path");
		StringTokenizer parser = new StringTokenizer(property, ";");
		while (parser.hasMoreTokens()) {
		    System.err.println(parser.nextToken());
		    }
		test.deRes(paramArrayOfByte, paramInt);
	}
	
}
