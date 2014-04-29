package com.lezo.encrypt;

public interface Encryptor {
	public String encript(byte[] source) throws Exception;

	public String encript(byte[] source, String key) throws Exception;
}
