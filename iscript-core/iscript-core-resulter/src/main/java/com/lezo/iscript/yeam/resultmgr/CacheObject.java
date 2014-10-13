package com.lezo.iscript.yeam.resultmgr;

import java.util.Calendar;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class CacheObject {
	private String key;
	private Object value;
	private Long creation;
	private Long timeTo;
	private Long stamp = 0L;

	public CacheObject(String key, Long timeTo) {
		this(key, "", System.currentTimeMillis(), timeTo);
	}

	public CacheObject(String key, Object value, Long creation, Long timeTo) {
		super();
		this.key = key;
		this.value = value;
		this.creation = creation;
		this.timeTo = timeTo;
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, -30);
		this.stamp = c.getTimeInMillis();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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
		CacheObject other = (CacheObject) obj;
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

	public Object getValue() {
		return value;
	}

	public synchronized void setValue(Object value) {
		this.value = value;
	}
}
