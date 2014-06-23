package com.lezo.iscript.utils.encrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;


import sun.misc.BASE64Encoder;

public class Base64Encryptor implements Encryptor {
	private BASE64Encoder encoder = new BASE64Encoder();

	@Override
	public String encript(byte[] source) throws Exception {
		InputStream in = new ByteArrayInputStream(source);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			encoder.encode(in, out);
			out.flush();
			return new String(out.toByteArray());
		} catch (Exception e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public String encript(byte[] source, String key) throws Exception {
		return encript(source);
	}

}
