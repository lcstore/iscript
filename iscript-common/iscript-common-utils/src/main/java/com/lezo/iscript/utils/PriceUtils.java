package com.lezo.iscript.utils;

public class PriceUtils {
	private static final float CENT_VALUE = 100F;

	public static Long toCentPrice(Float originPrice) {
		if (originPrice == null) {
			return null;
		}
		return (long) (originPrice * CENT_VALUE);
	}

	public static Float toPrice(Long centPrice) {
		if (centPrice == null) {
			return null;
		}
		return (centPrice / CENT_VALUE);
	}
}
