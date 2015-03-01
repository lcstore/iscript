package com.lezo.iscript.utils.queue;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LevelObject implements Priorityable {
	private int level;
	private String name;

	public LevelObject(int level, String name) {
		super();
		this.level = level;
		this.name = name;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(level).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LevelObject other = (LevelObject) obj;
		return new EqualsBuilder().append(level, other.getLevel()).isEquals();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPriority() {
		return level;
	}

}
