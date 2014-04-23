package com.lezo.iscript.font;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FontVector {
	private FontDot dot;
	private Set<FontLine> lineSet;

	public FontVector(FontDot dot, Set<FontLine> lineSet) {
		super();
		this.dot = dot;
		this.lineSet = lineSet;
	}

	public FontDot getDot() {
		return dot;
	}

	public Set<FontLine> getLineSet() {
		return lineSet;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(dot).append(lineSet).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FontVector other = (FontVector) obj;
		return new EqualsBuilder().append(dot, other.getDot()).append(lineSet, other.getLineSet()).isEquals();
	}

}
