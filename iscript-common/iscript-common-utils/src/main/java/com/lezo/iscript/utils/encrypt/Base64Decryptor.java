package com.lezo.iscript.utils.encrypt;

import org.apache.commons.codec.binary.Base64;

public class Base64Decryptor implements Decryptor {
	private Base64 decoder = new Base64();

	@Override
	public String decript(byte[] source) throws Exception {
		return new String(decoder.decode(source));
	}

	@Override
	public String decript(byte[] source, String key) throws Exception {
		return decript(source);
	}
}
