package com.lezo.iscript.utils;

import java.util.Random;
import java.util.UUID;

public class SessionUtils {
	private static final String UN_WORD_REGEX = "[^0-9a-zA-Z]+";
	private static final int MAX_SESSION_LEN = 24;

	public static String randomSession() {
		String session = UUID.randomUUID().toString();
		session = session.replaceAll(UN_WORD_REGEX, "");
		int len = session.length();
		len = (len < MAX_SESSION_LEN) ? len : MAX_SESSION_LEN;
		session = session.substring(0, len);
		for (int i = len; i < MAX_SESSION_LEN; i++) {
			session += new Random(System.currentTimeMillis()).nextInt(10);
		}
		return session;
	}
}
