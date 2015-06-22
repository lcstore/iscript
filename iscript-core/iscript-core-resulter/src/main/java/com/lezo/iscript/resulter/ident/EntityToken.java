package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Data;

import org.apache.commons.collections4.CollectionUtils;

@Data
public class EntityToken {
	private SectionToken master;
	private List<SectionToken> assists;

	public EntityToken(String value) {
		this("EntityToken", value);
	}

	public EntityToken(String key, String value) {
		this.master = new SectionToken(key, value, null);
		this.assists = new ArrayList<SectionToken>();
	}

	public void addAssistToken(SectionToken token) {
		token.setParent(getMaster());
		this.assists.add(token);
	}

	public static List<SectionToken> getTokensByTokenizer(List<SectionToken> leaveList, String tokenizer) {
		List<SectionToken> destList = new ArrayList<SectionToken>(leaveList.size());
		for (SectionToken token : leaveList) {
			String curTokenizer = token.getTokenizer();
			if (tokenizer.equals(curTokenizer)) {
				destList.add(token);
			}
		}
		return destList;
	}

	public static void getLeveChildren(List<SectionToken> leaveList, SectionToken token) {
		List<SectionToken> children = token.getChildren();
		if (CollectionUtils.isEmpty(children)) {
			leaveList.add(token);
			return;
		}
		for (SectionToken child : children) {
			getLeveChildren(leaveList, child);
		}
	}

	public static void sortTokenLengthDesc(List<SectionToken> tokenList) {
		if (CollectionUtils.isEmpty(tokenList)) {
			return;
		}
		Collections.sort(tokenList, new Comparator<SectionToken>() {
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
	}

}
