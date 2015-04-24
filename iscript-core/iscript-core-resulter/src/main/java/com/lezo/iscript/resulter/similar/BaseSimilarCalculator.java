package com.lezo.iscript.resulter.similar;

import com.lezo.iscript.resulter.token.CharPart;

public class BaseSimilarCalculator implements SimilarCalculator {

	@Override
	public Float calcSimilar(CharPart leftPart, CharPart rightPart) {
		BrandSimilarCalculator brandSimilarCalculator = new BrandSimilarCalculator();
		SpecSimilarCalculator specSimilarCalculator = new SpecSimilarCalculator();
		CharSimilarCalculator similarCalculator = new CharSimilarCalculator();
		Float brandSimilar = brandSimilarCalculator.calcSimilar(leftPart, rightPart);
		Float specSimilar = specSimilarCalculator.calcSimilar(leftPart, rightPart);
		Float charSimilar = similarCalculator.calcSimilar(leftPart, rightPart);
		Float specRatio = specSimilar > 0 ? 0.2F : 0;
		System.out.println("#brandSimilar:" + brandSimilar + ",specSimilar:" + specSimilar + ",charSimilar:"
				+ charSimilar);
		return brandSimilar * 0.5F + specSimilar * specRatio + charSimilar * (0.5F - specRatio);
	}

}
