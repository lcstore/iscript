package com.lezo.iscript.resulter.ident;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class AttrTokenizer extends AbstractTokenizer {

	private void doAssistToken(EntityToken entity) {
		SectionToken master = entity.getMaster();
		List<SectionToken> assists = entity.getAssists();
		Collections.sort(assists, new Comparator<SectionToken>() {
			@Override
			public int compare(SectionToken o1, SectionToken o2) {
				if (o1.getValue() == null) {
					return -1;
				}
				if (o2.getValue() == null) {
					return 1;
				}
				return o2.getValue().length() - o1.getValue().length();
			}
		});
		String mString = master.getValue();
		for (SectionToken assist : assists) {
			if (StringUtils.isBlank(mString)) {
				break;
			}
			String value = assist.getValue();
			if (StringUtils.isBlank(value)) {
				continue;
			}
			if (mString.contains(value)) {
				String key = master.getKey() + "_" + assist.getKey();
				SectionToken child = new SectionToken(key, value.trim(), this.getClass().getName());
				master.addChild(child);
			}
		}
	}

	@Override
	protected void doToken(List<EntityToken> entityList) {
		for (EntityToken entity : entityList) {
			doAssistToken(entity);
		}
	}

}
