package com.lezo.iscript.utils.encrypt;

import org.apache.commons.codec.binary.Base64;

public class Base64Encryptor implements Encryptor {
	private Base64 encoder = new Base64();

	@Override
	public String encript(byte[] source) throws Exception {
		return new String(encoder.encode(source));
	}

	@Override
	public String encript(byte[] source, String key) throws Exception {
		return encript(source);
	}

}
