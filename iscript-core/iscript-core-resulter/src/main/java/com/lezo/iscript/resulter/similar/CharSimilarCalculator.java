package com.lezo.iscript.resulter.similar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.resulter.token.CharPart;
import com.lezo.iscript.resulter.token.CharPartUtils;
import com.lezo.iscript.resulter.token.SplitTokenizer;

public class CharSimilarCalculator implements SimilarCalculator {

	@Override
	public Float calcSimilar(CharPart leftPart, CharPart rightPart) {
		List<CharPart> lLeafParts = new ArrayList<CharPart>();
		List<CharPart> rLeafParts = new ArrayList<CharPart>();
		CharPartUtils.findLastLevelCharParts(leftPart, lLeafParts);
		CharPartUtils.findLastLevelCharParts(rightPart, rLeafParts);

		List<CharPart> lNormParts = new ArrayList<CharPart>();
		List<CharPart> rNormParts = new ArrayList<CharPart>();
		CharPartUtils.findWithTokenizer(lLeafParts, lNormParts, SplitTokenizer.class);
		CharPartUtils.findWithTokenizer(rLeafParts, rNormParts, SplitTokenizer.class);
		List<SimiarPart> similarList = new ArrayList<SimiarPart>();
		Set<CharPart> noSimilarList = new HashSet<CharPart>();
		Set<CharPart> uCharParts = new HashSet<CharPart>();
		for (CharPart lNorm : lNormParts) {
			CharPart mostSimilarPart = null;
			String maxCmmChar = null;
			for (CharPart rNorm : rNormParts) {
				String commonChars = CharPartUtils.getLongestCommonChars(lNorm.getToken(), rNorm.getToken());
				if (commonChars == null) {
					continue;
				}
				if (maxCmmChar == null || maxCmmChar.length() < commonChars.length()) {
					maxCmmChar = commonChars;
					mostSimilarPart = rNorm;
				}
			}
			if (mostSimilarPart == null) {
				noSimilarList.add(lNorm);
				System.err.println(lNorm.getToken());
				continue;
			}
			uCharParts.add(mostSimilarPart);
			SimiarPart part = new SimiarPart();
			part.setLeftString(lNorm.getToken());
			part.setRightString(mostSimilarPart.getToken());
			int minLen = Math.min(part.getLeftString().length(), part.getRightString().length());
			int distance = minLen == maxCmmChar.length() ? 0 : StringUtils.getLevenshteinDistance(part.getLeftString(),
					part.getRightString());
			Float score = maxCmmChar.length() * 1F / (maxCmmChar.length() + distance);
			part.setScore(score);
			similarList.add(part);
		}
		int noSimilarCount = noSimilarList.size();
		for (CharPart rNorm : rNormParts) {
			if (!uCharParts.contains(rNorm)) {
				noSimilarCount++;
			}
		}
		Float sumScore = 0F;
		for (SimiarPart sPart : similarList) {
			sumScore += sPart.getScore();
		}
		System.err.println(similarList);
		Float score = similarList.isEmpty() ? 0F : sumScore / similarList.size();
		score = noSimilarCount > 0 ? (score - noSimilarCount * 1F / (noSimilarCount + 2 * similarList.size())) : score;
		return score;
	}

}
