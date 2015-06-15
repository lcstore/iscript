package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import lombok.Data;

@Data
public class SectionToken {
	private String key;
	private String value;
	private String division;
	/**
	 * 可信度
	 */
	private int trust;
	/**
	 * 切词器
	 */
	private String tokenizer;
	private SectionToken parent;
	private List<SectionToken> children;

	public SectionToken(String key, String value) {
		this(key, value, "");
	}

	public SectionToken(String key, String value, String tokenizer) {
		this.key = key;
		this.value = value;
		this.division = value;
		this.tokenizer = tokenizer;
	}

	public SectionToken addChild(SectionToken child) {
		if (child == null) {
			throw new IllegalArgumentException("child must not be null");
		}
		if (this.children == null) {
			this.children = new ArrayList<SectionToken>();
		}
		child.setParent(this);
		this.children.add(child);
		return child;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SectionToken other = (SectionToken) obj;
		return new EqualsBuilder().append(key, other.getKey()).append(value, other.getValue())
				.append(tokenizer, other.getTokenizer()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(key).append(value).append(tokenizer).toHashCode();
	}

	@Override
	public String toString() {
		return "SectionToken [key=" + key + ", value=" + value + ", division=" + division + ", trust=" + trust
				+ ", tokenizer=" + tokenizer + "]";
	}
}
