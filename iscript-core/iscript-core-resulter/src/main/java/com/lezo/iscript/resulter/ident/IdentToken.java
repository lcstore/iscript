package com.lezo.iscript.resulter.ident;

import java.util.Set;

import lombok.Data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Data
public class IdentToken {
	public static final int SOURCE_NORMAL = 0;
	public static final int SOURCE_RANGE = 1;
	public static final int SOURCE_FIELD = 2;
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
	private Set<String> synonyms = java.util.Collections.emptySet();
	/**
	 * 切词来源
	 */
	private int source = SOURCE_NORMAL;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentToken other = (IdentToken) obj;
		return new EqualsBuilder().append(getToken(), other.getToken()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(token).toHashCode();
	}
}
