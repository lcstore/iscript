package com.lezo.iscript.resulter.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class SplitTokenizer implements ClustTokenizer {

	@Override
	public List<CharPart> doToken(List<CharPart> sourceList) {
		List<CharPart> destList = new ArrayList<CharPart>();
		CharPartUtils.findExcludeTokenizer(sourceList, destList,SplitTokenizer.class);
		for (CharPart charPart : destList) {
			if (CollectionUtils.isEmpty(charPart.getChildList())) {
				return Collections.emptyList();
			}
			Collections.sort(charPart.getChildList(), new Comparator<CharPart>() {
				@Override
				public int compare(CharPart o1, CharPart o2) {
					String lToken = o1.getToken();
					String rToken = o2.getToken();
					return rToken.length() - lToken.length();
				}
			});
			String tmpSource = charPart.getToken();
			String spliter = "__<@_@>__";
			for (CharPart subPart : charPart.getChildList()) {
				tmpSource = tmpSource.replace(subPart.getToken(), spliter);
			}
			tmpSource = tmpSource.replaceAll("\\s+", spliter);
			String[] unitArr = tmpSource.split(spliter);
			if (unitArr == null) {
				return Collections.emptyList();
			}
			List<CharPart> splitList = charPart.getChildList();
			for (String unit : unitArr) {
				if (!StringUtils.isBlank(unit)) {
					CharPart newPart = new CharPart();
					newPart.setToken(unit);
					newPart.setFromIndex(tmpSource.indexOf(unit));
					newPart.setTokenizer(this);
					splitList.add(newPart);
				}
			}
		}
		return sourceList;
	}
}
