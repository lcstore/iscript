package com.lezo.iscript.font;

import java.awt.Point;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FontDot {
	private Point pt;
	private int value;
	private int times;

	public FontDot(Point pt) {
		super();
		this.pt = pt;
	}

	public Point getPt() {
		return pt;
	}

	public void setPt(Point pt) {
		this.pt = pt;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}
	
	@Override
	public String toString() {
		return "(" + pt.x + "," + pt.y + "," + value + "," + times + ")";
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(pt).append(times).append(value).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FontDot other = (FontDot) obj;
		return new EqualsBuilder().append(pt, other.getPt()).append(times, other.getTimes())
				.append(value, other.getValue()).isEquals();
	}

	public boolean after(FontDot dot) {
		return pt.y >= dot.getPt().y || (pt.y == dot.getPt().y && pt.x >= dot.getPt().x);
	}
}
