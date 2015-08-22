package com.lezo.iscript;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

import com.lezo.iscript.io.IFetcher;
import com.lezo.iscript.io.IoConstants;

@Getter
@Setter
public class IoSeed {
	public static final String DIR_SEPARATOR = "/";
	private String type;
	private int level = IoConstants.LEVEL_PATH;
	private IFetcher fetcher;
	private String domain;
	private String bucket;
	private String dataPath;
	private int retry;
	private Map<String, String> params;

	public String toKey() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.bucket);
		sb.append(".");
		sb.append(this.domain);
		sb.append(":");
		sb.append(this.dataPath);
		return sb.toString();
	}

	public String getType() {
		if (type == null) {
			StringTokenizer tokenizer = new StringTokenizer(getDataPath(), DIR_SEPARATOR);
			Pattern pattern = Pattern.compile("^[0-9]+$");
			while (tokenizer.hasMoreElements()) {
				String value = tokenizer.nextElement().toString();
				Matcher matcher = pattern.matcher(value);
				if (matcher.find()) {
					type = tokenizer.nextElement().toString();
					break;
				}
			}
		}
		return type;
	}
}