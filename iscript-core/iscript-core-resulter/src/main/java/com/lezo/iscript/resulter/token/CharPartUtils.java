package com.lezo.iscript.resulter.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class CharPartUtils {

	public static void findWithTokenizer(List<CharPart> sourceList, List<CharPart> destList,
			Class<? extends ClustTokenizer> tokenClass) {
		if (sourceList == null) {
			return;
		}
		for (CharPart part : sourceList) {
			if (part.getTokenizer() == null) {
				continue;
			}
			if (part.getTokenizer().getClass().isAssignableFrom(tokenClass)) {
				destList.add(part);
			}
			if (CollectionUtils.isNotEmpty(part.getChildList())) {
				findWithTokenizer(part.getChildList(), destList, tokenClass);
			}
		}
	}

	public static void findLastLevelCharParts(List<CharPart> sourceList, List<CharPart> destList) {
		if (sourceList == null) {
			return;
		}
		for (CharPart part : sourceList) {
			if (part.getChildList() == null) {
				destList.add(part);
			} else {
				findLastLevelCharParts(part.getChildList(), destList);
			}
		}
	}

	public static void findExcludeTokenizer(List<CharPart> sourceList, List<CharPart> destList,
			Class<? extends ClustTokenizer> tokenClass) {
		if (sourceList == null) {
			return;
		}
		for (CharPart part : sourceList) {
			if (part.getChildList() == null) {
				continue;
			}
			boolean bExclude = true;
			for (CharPart cPart : part.getChildList()) {
				if (cPart.getTokenizer() == null) {
					continue;
				}
				if (cPart.getTokenizer().getClass().isAssignableFrom(tokenClass)) {
					bExclude = false;
					break;
				}
			}
			if (bExclude) {
				destList.add(part);
			} else {
				findExcludeTokenizer(part.getChildList(), destList, tokenClass);
			}
		}
	}

	public static void findWithTokenizer(CharPart srcPart, List<CharPart> destList,
			Class<? extends ClustTokenizer> tokenClass) {
		if (srcPart == null) {
			return;
		}
		List<CharPart> sourceList = new ArrayList<CharPart>(1);
		sourceList.add(srcPart);
		findWithTokenizer(sourceList, destList, tokenClass);
	}

	public static void findLastLevelCharParts(CharPart srcPart, List<CharPart> destList) {
		if (srcPart == null) {
			return;
		}
		List<CharPart> sourceList = new ArrayList<CharPart>(1);
		sourceList.add(srcPart);
		findLastLevelCharParts(sourceList, destList);
	}

	public static void findExcludeTokenizer(CharPart srcPart, List<CharPart> destList,
			Class<? extends ClustTokenizer> tokenClass) {
		if (srcPart == null) {
			return;
		}
		List<CharPart> sourceList = new ArrayList<CharPart>(1);
		sourceList.add(srcPart);
		findLastLevelCharParts(sourceList, destList);
	}

	public static String getLongestCommonChars(String lString, String rString) {
		List<String> cmmList = getCommonChars(lString, rString);
		if (cmmList.isEmpty()) {
			return null;
		}
		Collections.sort(cmmList, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o2.length() - o1.length();
			}
		});
		return cmmList.get(0);
	}

	public static List<String> getCommonChars(String lString, String rString) {
		if (StringUtils.isEmpty(lString) || StringUtils.isEmpty(rString)) {
			return Collections.emptyList();
		}
		List<String> cmmList = new ArrayList<String>();
		int lLen = lString.length();
		for (int i = 0; i < lLen;) {
			int step = 0;
			int index = rString.indexOf(lString.charAt(i + step));
			int lastIndex = index;
			while (index >= 0 && (i + step + 1) < lLen) {
				index = rString.indexOf(lString.charAt(i + step + 1));
				if (lastIndex + 1 == index) {
					step++;
					lastIndex = index;
				} else {
					break;
				}
			}
			if (step > 0) {
				cmmList.add(lString.substring(i, i + step + 1));
				i = i + step;
			}
			i++;
		}
		return cmmList;
	}

}
