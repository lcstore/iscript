package com.lezo.iscript.yeam.resultmgr;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class ValidKey {
	private String key;
	private String attribute;
	private Long creation;
	private Long timeTo;
	private Long stamp = 0L;

	public ValidKey(String key, Long timeTo) {
		this.key = key;
		this.timeTo = timeTo;
		this.creation = System.currentTimeMillis();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Long getStamp() {
		return stamp;
	}

	public synchronized void setStamp(Long stamp) {
		this.stamp = stamp;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidKey other = (ValidKey) obj;
		return new EqualsBuilder().append(key, other.getKey()).isEquals();
	}

	public Long getCreation() {
		return creation;
	}

	public void setCreation(Long creation) {
		this.creation = creation;
	}

	public boolean isTimeOut() {
		return System.currentTimeMillis() >= this.timeTo;
	}
}
