package com.lezo.iscript.utils.encrypt;

public interface Decryptor {
	public String decript(byte[] source) throws Exception;

	public String decript(byte[] source, String key) throws Exception;
}
