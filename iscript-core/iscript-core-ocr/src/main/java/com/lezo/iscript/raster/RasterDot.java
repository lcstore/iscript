package com.lezo.iscript.raster;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RasterDot {
	private int x;
	private int y;
	private double value;

	public RasterDot(int x, int y, double value) {
		super();
		this.x = x;
		this.y = y;
		this.value = value;
	}

	public RasterDot(int x, int y) {
		this(x, y, 0);
	}

	public int compareTo(RasterDot dot) {
		int yCom = y - dot.getY();
		return (yCom == 0) ? (x - dot.getX()) : yCom;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(x).append(y).append(value).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RasterDot other = (RasterDot) obj;
		return new EqualsBuilder().append(x, other.getX()).append(y, other.getY()).append(value, other.getValue())
				.isEquals();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
