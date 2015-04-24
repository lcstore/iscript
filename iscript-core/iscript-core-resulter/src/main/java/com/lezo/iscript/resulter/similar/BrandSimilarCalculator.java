package com.lezo.iscript.resulter.similar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lezo.iscript.resulter.token.BrandTokenizer;
import com.lezo.iscript.resulter.token.CharPart;
import com.lezo.iscript.resulter.token.CharPartUtils;

public class BrandSimilarCalculator implements SimilarCalculator {

	@Override
	public Float calcSimilar(CharPart leftPart, CharPart rightPart) {
		List<CharPart> lSourceParts = new ArrayList<CharPart>(1);
		lSourceParts.add(leftPart);
		List<CharPart> rSourceParts = new ArrayList<CharPart>(1);
		rSourceParts.add(rightPart);

		List<CharPart> lBrandParts = new ArrayList<CharPart>();
		CharPartUtils.findWithTokenizer(lSourceParts, lBrandParts, BrandTokenizer.class);
		List<CharPart> rBrandParts = new ArrayList<CharPart>();
		CharPartUtils.findWithTokenizer(rSourceParts, rBrandParts, BrandTokenizer.class);
		Set<String> lBrandSet = new HashSet<String>();
		for (CharPart brand : lBrandParts) {
			lBrandSet.add(brand.getToken());
		}
		boolean bSame = false;
		for (CharPart brand : lBrandParts) {
			if (lBrandSet.contains(brand.getToken())) {
				bSame = true;
				break;
			}
		}
		return bSame ? 1F : -1F;
	}

}
