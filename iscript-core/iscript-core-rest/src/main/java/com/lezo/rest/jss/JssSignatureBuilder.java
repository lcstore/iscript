package com.lezo.rest.jss;

import java.util.Map;

import com.lezo.rest.SignBuildable;

public class JssSignatureBuilder implements SignBuildable {

	private static final String NEW_LINE = "\n";

	@Override
	public String getSign(Map<String, Object> inMap) throws Exception {
		// Signature = Base64( HMAC-SHA1( YourSecretKey, UTF-8-Encoding-Of(
		// StringToSign ) ) );
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP-Verb");
		sb.append(NEW_LINE);
		sb.append("Content-MD5");
		sb.append(NEW_LINE);
		sb.append("Content-Type");
		sb.append(NEW_LINE);
		sb.append("Date");
		sb.append("CanonicalizedHeaders");
		sb.append("CanonicalizedResource");
		return null;
	}

	private String getCanonicalizedResource(Map<String, Object> inMap) {
		return null;

	}
}
