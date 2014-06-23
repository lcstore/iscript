package com.lezo.encrypt;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import com.lezo.iscript.utils.encrypt.Base64Decryptor;
import com.lezo.iscript.utils.encrypt.Base64Encryptor;
import com.lezo.iscript.utils.encrypt.Encryptor;
import com.lezo.iscript.utils.encrypt.EncryptorFactory;

public class EncryptTest {

	@Test
	public void testMe() throws Exception {
		String inputStr = "简单加密";
		System.err.println("原文:/n" + inputStr);

		byte[] inputData = inputStr.getBytes();
		String code = new Base64Encryptor().encript(inputData);

		System.err.println("BASE64加密后:/n" + code);

		String output = new Base64Decryptor().decript(code.getBytes());

		String outputStr = output;

		System.err.println("BASE64解密后:/n" + outputStr);

		// 验证BASE64加密解密一致性
		assertEquals(inputStr, outputStr);

		// 验证MD5对于同一内容加密是否一致
		Encryptor md5Encryptor = EncryptorFactory.getEncryptor(EncryptorFactory.KEY_MD5);
		assertEquals(md5Encryptor.encript(inputData), md5Encryptor.encript(inputData));

		// 验证SHA对于同一内容加密是否一致
		Encryptor shaEncryptor = EncryptorFactory.getEncryptor(EncryptorFactory.KEY_SHA);
		assertEquals(shaEncryptor.encript(inputData), shaEncryptor.encript(inputData));

		String key = "124124";
		System.err.println("Mac密钥:/n" + key);

		// 验证HMAC对于同一内容，同一密钥加密是否一致
		Encryptor hmacEncryptor = EncryptorFactory.getEncryptor(EncryptorFactory.KEY_HMAC_MD5);
		assertEquals(hmacEncryptor.encript(inputData, key), hmacEncryptor.encript(inputData, key));

		BigInteger md5 = new BigInteger(md5Encryptor.encript(inputData).getBytes());
		System.err.println("MD5:/n" + md5.toString(16));

		BigInteger sha = new BigInteger(shaEncryptor.encript(inputData).getBytes());
		System.err.println("SHA:/n" + sha.toString(32));

		BigInteger mac = new BigInteger(hmacEncryptor.encript(inputData, key).getBytes());
		System.err.println("HMAC:/n" + mac.toString(16));
	}
}
