package com.lezo.iscript.resulter.similar;

import com.lezo.iscript.resulter.token.CharPart;

public interface SimilarCalculator {

	Float calcSimilar(CharPart leftPart, CharPart rightPart);
}
