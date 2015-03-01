package com.lezo.encrypt;

public interface Decryptor {
	public String decript(byte[] source) throws Exception;

	public String decript(byte[] source, String key) throws Exception;
}
