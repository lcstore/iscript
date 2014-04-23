package com.lezo.iscript;

import org.junit.Test;

public class MathTest {

	@Test
	public void testDoubleZero() {
		Double zero = 0D;
		double a = 1.01D;
		double b = 1.0100D;
		double sub = a - b;
		double myZero = 0.000000;
		System.out.println(sub);
		System.out.println(zero.equals(sub));
		System.out.println(zero.equals(myZero));

	}
}
