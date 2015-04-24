package com.lezo.iscript.resulter.token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BrandTokenizer implements ClustTokenizer {

	@Override
	public List<CharPart> doToken(List<CharPart> sourceList) {
		Set<String> brandSet = new HashSet<String>();
		brandSet.add("素手浣花");
		brandSet.add("费列罗");
		brandSet.add("ferrero");
		brandSet.add("罗杰生");
		for (CharPart srcPart : sourceList) {
			String source = srcPart.getToken();
			source = source.toLowerCase();
			for (String brand : brandSet) {
				int index = source.indexOf(brand);
				if (index < 0) {
					continue;
				}
				List<CharPart> childList = srcPart.getChildList();
				if (childList == null) {
					childList = new ArrayList<CharPart>();
					srcPart.setChildList(childList);
				}
				CharPart charPart = new CharPart();
				charPart.setToken(brand);
				charPart.setFromIndex(index);
				charPart.setTokenizer(this);
				childList.add(charPart);
			}
			keepLongest(srcPart);
		}
		return sourceList;
	}

	private void keepLongest(CharPart srcPart) {
		List<CharPart> childList = srcPart.getChildList();
		if (childList == null || childList.size() < 2) {
			return;
		}
		Iterator<CharPart> it = srcPart.getChildList().iterator();
		Set<CharPart> removeParts = new HashSet<CharPart>();
		while (it.hasNext()) {
			CharPart charPart = it.next();
			if (removeParts.contains(charPart)) {
				continue;
			}
			String token = charPart.getToken();
			int len = token.length();
			Iterator<CharPart> nextIt = srcPart.getChildList().iterator();
			while (nextIt.hasNext()) {
				CharPart nextPart = nextIt.next();
				if (charPart == nextPart || removeParts.contains(nextPart)) {
					continue;
				}
				String cmpValue = nextPart.getToken();
				if (cmpValue.length() > len && cmpValue.contains(token)) {
					removeParts.add(charPart);
					break;
				} else if (cmpValue.length() < len && token.contains(cmpValue)) {
					removeParts.add(nextPart);
					break;
				}

			}
		}
		if (!removeParts.isEmpty()) {
			List<CharPart> keepList = new ArrayList<CharPart>(childList.size() - removeParts.size());
			for (CharPart part : childList) {
				if (!removeParts.contains(part)) {
					keepList.add(part);
				}
			}
			srcPart.setChildList(keepList);
		}
	}
}
