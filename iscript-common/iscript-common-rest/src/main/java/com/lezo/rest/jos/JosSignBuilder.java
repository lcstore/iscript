package com.lezo.rest.jos;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lezo.rest.SignBuildable;

public class JosSignBuilder implements SignBuildable {
	private final static String CHARSET_NAME = "utf-8";
	private static List<String> constKeyList = new ArrayList<String>();
	private final static char CONST_CHARS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };
	private static String APP_SECRET = "app_secret";
	static {
		constKeyList.add("method");
	}

	@Override
	public String getSign(Map<String, Object> inMap) throws Exception {
		verifyMap(inMap);
		String appSecret = (String) inMap.remove(APP_SECRET);
		List<Entry<String, Object>> inList = new ArrayList<Entry<String, Object>>(inMap.entrySet());
		ascentOrder(inList);
		StringBuilder sb = new StringBuilder();
		sb.append(appSecret);
		for (Entry<String, Object> in : inList) {
			sb.append(in.getKey());
			sb.append(in.getValue());
		}
		sb.append(appSecret);
		String md5Value = md5Encode(sb.toString());
		return md5Value.toUpperCase();
	}

	private String md5Encode(String paramString) throws Exception {
		MessageDigest mdInst = MessageDigest.getInstance("MD5");
		mdInst.update(paramString.getBytes(CHARSET_NAME));
		// 执行哈希计算，获得密文
		byte[] mdArray = mdInst.digest();
		String hexChars = getHexChars(mdArray);
		return hexChars;
	}

	private String getHexChars(byte[] mdArray) {
		int len = mdArray.length;
		char newChars[] = new char[len * 2];
		int k = 0;
		for (int i = 0; i < len; i++) { // i = 0
			byte oneByte = mdArray[i]; // 95
			newChars[k++] = CONST_CHARS[oneByte >>> 4 & 0xf]; // 5
			newChars[k++] = CONST_CHARS[oneByte & 0xf]; // F
		}
		return new String(newChars);
	}

	private void ascentOrder(List<Entry<String, Object>> inList) {
		Collections.sort(inList, new Comparator<Entry<String, Object>>() {
			@Override
			public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
	}

	private void verifyMap(Map<String, Object> inMap) {
		for (String key : constKeyList) {
			if (!inMap.containsKey(key)) {
				throw new IllegalArgumentException("not found " + key + " in argument..");
			}
		}
	}

}
