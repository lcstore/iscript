package com.lezo.iscript.resulter.token;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CharPart {
	private List<CharPart> childList;
	private ClustTokenizer tokenizer;
	private Map<String, String> extendMap;
	private String token;
	private int fromIndex;

	@Override
	public String toString() {
		String sTokenizer = tokenizer == null ? "" : tokenizer.getClass().getSimpleName();
		return "CharPart {token=" + token + ", fromIndex=" + fromIndex + ",tokenizer=" + sTokenizer + ",childList="
				+ childList + "}";
	}
}
