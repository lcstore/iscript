package com.lezo.iscript.utils.encrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;


import sun.misc.BASE64Decoder;

public class Base64Decryptor implements Decryptor {
	private BASE64Decoder decoder = new BASE64Decoder();

	@Override
	public String decript(byte[] source) throws Exception {
		InputStream in = new ByteArrayInputStream(source);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			decoder.decodeBuffer(in, out);
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
	public String decript(byte[] source, String key) throws Exception {
		return decript(source);
	}
}
