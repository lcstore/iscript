package com.lezo.iscript.resulter.ident;

import java.util.Set;

import lombok.Data;

@Data
public class IdentToken {
	private int index;
	/**
	 * 识别器
	 */
	private Identifier identifier;
	/**
	 * 识别结果
	 */
	private String token;
	/**
	 * 同义词
	 */
	private Set<String> synonyms;
	/**
	 * 最好的、主要的
	 */
	private boolean prime = false;
}
