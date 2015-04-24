package com.lezo.iscript.resulter.token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecTokenizer implements ClustTokenizer {

	@Override
	public List<CharPart> doToken(List<CharPart> sourceList) {
		// Pattern oReg = Pattern.compile("[0-9a-zA-Z]");
		Pattern oReg = Pattern.compile("[^\u4e00-\u9fa5\\s]{2,}");
		for (CharPart srcPart : sourceList) {
			String source = srcPart.getToken();
			Matcher matcher = oReg.matcher(source);
			while (matcher.find()) {
				int index = matcher.start();
				List<CharPart> childList = srcPart.getChildList();
				if (childList == null) {
					childList = new ArrayList<CharPart>();
					srcPart.setChildList(childList);
				}
				CharPart charPart = new CharPart();
				charPart.setToken(matcher.group());
				charPart.setFromIndex(index);
				charPart.setTokenizer(this);
				childList.add(charPart);
			}
		}
		return sourceList;
	}
}
