package com.lezo.iscript.resulter.token;

import java.util.List;

public class CharPart {
	private List<CharPart> childList;
	private ClustTokenizer tokenizer;
	private String token;
	private int fromIndex;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getFromIndex() {
		return fromIndex;
	}

	public void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}

	public ClustTokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(ClustTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public List<CharPart> getChildList() {
		return childList;
	}

	public void setChildList(List<CharPart> childList) {
		this.childList = childList;
	}

	@Override
	public String toString() {
		String sTokenizer = tokenizer == null ? "" : tokenizer.getClass().getSimpleName();
		return "CharPart {token=" + token + ", fromIndex=" + fromIndex + ",tokenizer=" + sTokenizer + ",childList="
				+ childList + "}";
	}
}
