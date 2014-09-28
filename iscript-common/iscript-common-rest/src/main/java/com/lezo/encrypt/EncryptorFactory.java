package com.lezo.encrypt;

import java.security.MessageDigest;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.lezo.iscript.utils.encrypt.Encryptor;

public class EncryptorFactory {
	public static final String KEY_MD5 = "MD5";
	public static final String KEY_SHA = "SHA";

	public static final String KEY_HMAC_MD5 = "HmacMD5";
	public static final String KEY_HMAC_SHA1 = "HmacSHA1";
	public static final String KEY_HMAC_SHA256 = "HmacSHA256";
	public static final String KEY_HMAC_SHA384 = "HmacSHA384";
	public static final String KEY_HMAC_SHA512 = "HmacSHA512";

	public static Encryptor getEncryptor(final String algorithm) {
		if (KEY_MD5.equals(algorithm) || KEY_SHA.equals(algorithm)) {
			return new Encryptor() {
				@Override
				public String encript(byte[] source, String key) throws Exception {
					return encript(source);
				}

				@Override
				public String encript(byte[] source) throws Exception {
					MessageDigest mdObj = MessageDigest.getInstance(algorithm);
					mdObj.update(source);
					return new String(mdObj.digest());
				}
			};
		} else if (KEY_HMAC_MD5.equals(algorithm) || KEY_HMAC_SHA1.equals(algorithm)
				|| KEY_HMAC_SHA256.equals(algorithm) || KEY_HMAC_SHA384.equals(algorithm)
				|| KEY_HMAC_SHA512.equals(algorithm)) {
			return new Encryptor() {
				@Override
				public String encript(byte[] source, String key) throws Exception {
					if (key == null) {
						KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
						SecretKey secretKey = keyGenerator.generateKey();
						key = new String(secretKey.getEncoded());
					}
					byte[] initKey = key.getBytes();
					SecretKey secretKey = new SecretKeySpec(initKey, algorithm);
					Mac mac = Mac.getInstance(secretKey.getAlgorithm());
					mac.init(secretKey);
					return new String(mac.doFinal(source));
				}

				@Override
				public String encript(byte[] source) throws Exception {
					return encript(source, null);
				}
			};
		}
		return null;
	}

}
