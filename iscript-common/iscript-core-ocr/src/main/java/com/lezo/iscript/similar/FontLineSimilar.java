package com.lezo.iscript.similar;

import com.lezo.iscript.font.FontLine;

public class FontLineSimilar implements Similarable {
	private FontLine one;
	private FontLine other;

	public FontLineSimilar(FontLine one, FontLine other) {
		super();
		this.one = one;
		this.other = other;
	}

	@Override
	public double getSimilarity() {
		double result = 0;
		if (one.equals(other)) {
			result = 1.0;
			return result;
		}
		Double oneSlope = getSlope(one);
		Double otherSlope = getSlope(other);
		if (oneSlope == null && oneSlope == otherSlope) {
			if (FontLine.isZero(one.getArgA() - other.getArgA())) {
				result = 1.0;
			}
		} else if (oneSlope != null && oneSlope.equals(otherSlope)) {
			result = 1.0;
		}
		return result;
	}

	public Double getSlope(FontLine line) {
		if (FontLine.isZero(line.getArgB())) {
			return null;
		}
		return -line.getArgA() / line.getArgB();
	}

}
