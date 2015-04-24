package com.lezo.iscript.resulter.token;

import java.util.ArrayList;
import java.util.List;

public class CharTokenizer implements ClustTokenizer {

	@Override
	public List<CharPart> doToken(List<CharPart> sourceList) {
		BrandTokenizer brandTokenizer = new BrandTokenizer();
		SplitTokenizer splitTokenizer = new SplitTokenizer();
		brandTokenizer.doToken(sourceList);
		splitTokenizer.doToken(sourceList);
		List<CharPart> toSpecParts = new ArrayList<CharPart>();
		CharPartUtils.findWithTokenizer(sourceList, toSpecParts, SplitTokenizer.class);
		SpecTokenizer specTokenizer = new SpecTokenizer();
		specTokenizer.doToken(toSpecParts);
		splitTokenizer.doToken(sourceList);
		return sourceList;
	}

}
