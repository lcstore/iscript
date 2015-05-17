package com.lezo.encrypt;

import org.apache.commons.codec.binary.Base64;

import com.lezo.iscript.utils.encrypt.Encryptor;

public class Base64Encryptor implements Encryptor {
	private Base64 encoder = new Base64();

	@Override
	public String encript(byte[] source) throws Exception {
		byte[] dest = encoder.encode(source);
		return new String(dest);
	}

	@Override
	public String encript(byte[] source, String key) throws Exception {
		return encript(source);
	}

}
