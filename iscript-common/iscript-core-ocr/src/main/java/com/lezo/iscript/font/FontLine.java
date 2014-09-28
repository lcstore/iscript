package com.lezo.iscript.font;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FontLine {
	private static final double ZERO_ACCURACY = 1e-10;
	private FontDot startDot;
	private FontDot endDot;
	private double argA;
	private double argB;
	private double argC;

	public FontLine(FontDot startDot, FontDot endDot) {
		super();
		this.startDot = startDot;
		this.endDot = endDot;
		this.initLine();
	}

	public void initLine() {
		double subX = endDot.getPt().x - startDot.getPt().x;
		double subY = endDot.getPt().y - startDot.getPt().y;
		if (isZero(subX)) {
			argA = -1;
			argB = 0;
			argC = startDot.getPt().x;
		} else {
			argA = subY / subX;
			argB = -1;
			argC = startDot.getPt().y - argA * startDot.getPt().x;
		}
	}

	public static FontLine getFontLine(FontDot one, FontDot other) {
		if (one.after(other)) {
			return new FontLine(other, one);
		}
		return new FontLine(one, other);
	}

	public static boolean isZero(double source) {
		return (source > -ZERO_ACCURACY) && (source < ZERO_ACCURACY);
	}

	public boolean contains(FontDot dot) {
		if (!inLine(dot)) {
			return false;
		}
		return inRange(dot);
	}

	public boolean inLine(FontDot dot) {
		if (dot == null) {
			return false;
		}
		double lineResult = argA * dot.getPt().x + argB * dot.getPt().y + argC;
		return isZero(lineResult);
	}

	public boolean inRange(FontDot dot) {
		if (dot == null) {
			return false;
		}
		if (!inRange(startDot.getPt().x, endDot.getPt().x, dot.getPt().x)) {
			return false;
		}
		return inRange(startDot.getPt().y, endDot.getPt().y, dot.getPt().y);
	}

	public boolean inRange(int aSource, int bSource, int cValue) {
		int max = aSource;
		int min = bSource;
		if (aSource < bSource) {
			max = bSource;
			min = aSource;
		}
		return min <= cValue && cValue <= max;
	}

	public Double getSlope() {
		Double zero = 0D;
		if (zero.equals(argB)) {
			return Math.PI / 2;
		}
		double curK = -argA / argB;
		return Math.atan(curK);
	}

	public double getLen() {
		return startDot.getPt().distance(endDot.getPt());
	}

	public boolean isParallel(FontLine line) {
		if (line == null) {
			return false;
		}
		Double zero = 0D;
		return zero.equals(getSlope() - line.getSlope());
	}

	public FontDot getStartDot() {
		return startDot;
	}

	public void setStartDot(FontDot startDot) {
		this.startDot = startDot;
	}

	public FontDot getEndDot() {
		return endDot;
	}

	public void setEndDot(FontDot endDot) {
		this.endDot = endDot;
	}

	public double getArgA() {
		return argA;
	}

	public void setArgA(double argA) {
		this.argA = argA;
	}

	public double getArgB() {
		return argB;
	}

	public void setArgB(double argB) {
		this.argB = argB;
	}

	public double getArgC() {
		return argC;
	}

	public void setArgC(double argC) {
		this.argC = argC;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(startDot).append(endDot).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FontLine other = (FontLine) obj;
		return new EqualsBuilder().append(startDot, other.getStartDot()).append(endDot, other.getEndDot()).isEquals();
	}

	@Override
	public String toString() {
		return "[" + startDot + "," + endDot + "]";
	}

}
