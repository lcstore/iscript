package com.lezo.iscript.resulter.similar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.resulter.token.CharPart;
import com.lezo.iscript.resulter.token.CharPartUtils;
import com.lezo.iscript.resulter.token.SpecTokenizer;

public class SpecSimilarCalculator implements SimilarCalculator {

	private static final float RATIO_VALUE = 0.8F;

	@Override
	public Float calcSimilar(CharPart leftPart, CharPart rightPart) {
		List<CharPart> lParts = new ArrayList<CharPart>();
		List<CharPart> rParts = new ArrayList<CharPart>();
		CharPartUtils.findWithTokenizer(leftPart, lParts, SpecTokenizer.class);
		CharPartUtils.findWithTokenizer(rightPart, rParts, SpecTokenizer.class);
		List<SimiarPart> simiarPartList = new ArrayList<SimiarPart>();
		Set<CharPart> lNoRelateSet = new HashSet<CharPart>();
		Set<CharPart> rRelatePartSet = new HashSet<CharPart>();
		for (CharPart lNorm : lParts) {
			CharPart rRelatePart = null;
			String maxCmmChar = null;
			for (CharPart rNorm : rParts) {
				String commonChars = CharPartUtils.getLongestCommonChars(lNorm.getToken(), rNorm.getToken());
				if (commonChars == null) {
					continue;
				}
				if (maxCmmChar == null || maxCmmChar.length() < commonChars.length()) {
					maxCmmChar = commonChars;
					rRelatePart = rNorm;
				}
			}
			if (rRelatePart == null) {
				lNoRelateSet.add(lNorm);
				continue;
			}
			rRelatePartSet.add(rRelatePart);
			SimiarPart part = new SimiarPart();
			part.setLeftString(lNorm.getToken());
			part.setRightString(rRelatePart.getToken());
			part.setCommonString(maxCmmChar);
			int minLen = Math.min(part.getLeftString().length(), part.getRightString().length());
			int distance = minLen == maxCmmChar.length() ? 0 : StringUtils.getLevenshteinDistance(part.getLeftString(),
					part.getRightString());
			Float score = maxCmmChar.length() * 1F / (maxCmmChar.length() + distance);
			part.setScore(score);
			simiarPartList.add(part);
		}
		Set<CharPart> rNoRelateSet = new HashSet<CharPart>(rParts.size() - rRelatePartSet.size());
		for (CharPart rPart : rParts) {
			if (!rRelatePartSet.contains(rPart)) {
				rNoRelateSet.add(rPart);
			}
		}
		int numCount = 0;
		Pattern oNumReg = Pattern.compile("[0-9\\.]+");
		for (SimiarPart sPart : simiarPartList) {
			Matcher matcher = oNumReg.matcher(sPart.getCommonString());
			while (matcher.find()) {
				numCount++;
			}
		}
		int noRelateNumCount = 0;
		for (CharPart noRelatePart : lNoRelateSet) {
			Matcher matcher = oNumReg.matcher(noRelatePart.getToken());
			while (matcher.find()) {
				noRelateNumCount++;
			}
		}

		for (CharPart noRelatePart : rNoRelateSet) {
			Matcher matcher = oNumReg.matcher(noRelatePart.getToken());
			while (matcher.find()) {
				noRelateNumCount++;
			}
		}
		int totalNumCount = numCount + noRelateNumCount;

		Float sumScore = 0F;
		for (SimiarPart sPart : simiarPartList) {
			sumScore += sPart.getScore();
		}
		int indiePartCount = simiarPartList.size() * 2 + rNoRelateSet.size() + lNoRelateSet.size();
		sumScore = sumScore * (simiarPartList.size() * 2F + numCount) / (indiePartCount + totalNumCount);
		return sumScore;
	}

}
