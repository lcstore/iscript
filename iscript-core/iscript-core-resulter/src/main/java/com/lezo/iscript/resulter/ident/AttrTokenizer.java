package com.lezo.iscript.resulter.ident;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class AttrTokenizer extends AbstractTokenizer {
	private static final String DIVISION_MARK = "_@DIVISION@_";

	@Override
	protected void doToken(List<EntityToken> entityList) {

	}

	@Override
	protected void division(List<EntityToken> entityList) {
		divisionEntityAssists(entityList);
		// 同款词性，最小重合词进行分词。
		// divisionCluster(entityList);
	}

	private void divisionEntityAssists(List<EntityToken> entityList) {
		for (EntityToken entity : entityList) {
			divisionEntityAssists(entity);
		}
	}

	private void divisionEntityAssists(EntityToken entity) {
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
		for (SectionToken assist : assists) {
			if (StringUtils.isBlank(master.getDivision())) {
				break;
			}
			String value = assist.getValue();
			if (StringUtils.isBlank(value)) {
				continue;
			}
			String newValue = master.getDivision().replace(value, DIVISION_MARK);
			if (!newValue.equals(master.getDivision()) && !DIVISION_MARK.equals(newValue)) {
				String key = master.getKey() + "_" + assist.getKey();
				SectionToken child = new SectionToken(key, value.trim(), this.getClass().getName());
				master.addChild(child);
				master.setDivision(newValue);
			}
		}
		handleDivision(master);
	}

	private void handleDivision(SectionToken master) {
		String[] splitArrays = master.getDivision().split(DIVISION_MARK);
		if (splitArrays == null || splitArrays.length < 2) {
			return;
		}
		// delim contains a char,this char will not be token
		// StringTokenizer tokenizer
		for (String value : splitArrays) {
			if (StringUtils.isBlank(value)) {
				continue;
			}
			int index = master.getChildren() == null ? 0 : master.getChildren().size();
			index += 1;
			String key = master.getKey() + "_" + index;
			SectionToken child = new SectionToken(key, value.trim(), this.getClass().getName());
			master.addChild(child);
		}
	}
}
