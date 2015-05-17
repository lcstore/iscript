package com.lezo.encrypt;

import org.apache.commons.codec.binary.Base64;

import com.lezo.iscript.utils.encrypt.Decryptor;

public class Base64Decryptor implements Decryptor {
	private Base64 decoder = new Base64();

	@Override
	public String decript(byte[] source) throws Exception {
		byte[] dest = decoder.decode(source);
		return new String(dest);
	}

	@Override
	public String decript(byte[] source, String key) throws Exception {
		return decript(source);
	}
}
