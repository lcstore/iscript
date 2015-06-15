package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Data;

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

	public List<SectionToken> getTokensByTokenizer(List<SectionToken> leaveList, String tokenizer) {
		List<SectionToken> destList = new ArrayList<SectionToken>(leaveList.size());
		for (SectionToken token : leaveList) {
			if (tokenizer.equals(token.getTokenizer())) {
				destList.add(token);
			}
		}
		return destList;
	}

	public void getLeveChildren(List<SectionToken> leaveList, SectionToken token) {
		List<SectionToken> children = token.getChildren();
		if (CollectionUtils.isEmpty(children)) {
			leaveList.add(token);
			return;
		}
		for (SectionToken child : children) {
			getLeveChildren(leaveList, child);
		}
	}
}
