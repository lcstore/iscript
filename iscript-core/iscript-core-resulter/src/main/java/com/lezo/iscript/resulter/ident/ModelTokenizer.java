package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.utils.CharsUtils;

public class ModelTokenizer extends AbstractTokenizer {
	private static final Set<String> modelAssistKeySet = new HashSet<String>();
	private static final Pattern MODEL_REG = Pattern.compile("[^\u4e00-\u9fa5\\s\\(\\)（）]{2,}");
	static {
		modelAssistKeySet.add("productModel");
		modelAssistKeySet.add("型号");
		modelAssistKeySet.add("商品型号");
		modelAssistKeySet.add("产品型号");
	}

	@Override
	protected void doToken(List<EntityToken> entityTokens) {

		for (EntityToken entity : entityTokens) {
			List<SectionToken> tokenList = doToken(entity);
			Set<String> stableSet = getStableSet(entity);
			for (SectionToken token : tokenList) {
				if (stableSet.contains(CharsUtils.unifyChars(token.getValue()))) {
					continue;
				}
				token.getParent().addChild(token);
			}
		}
	}

	private List<SectionToken> doToken(EntityToken entity) {
		Set<SectionToken> tokenSet = new HashSet<SectionToken>();
		tokenAssist(tokenSet, entity);
		tokenChildren(tokenSet, entity);
		return new ArrayList<SectionToken>(tokenSet);
	}

	private void tokenChildren(Set<SectionToken> tokenSet, EntityToken entity) {
		List<SectionToken> leaveList = new ArrayList<SectionToken>();
		EntityToken.getLeveChildren(leaveList, entity.getMaster());
		for (SectionToken at : leaveList) {
			Matcher matcher = MODEL_REG.matcher(at.getValue());
			while (matcher.find()) {
				SectionToken newToken = new SectionToken(at.getKey(), matcher.group());
				newToken.setParent(at);
				newToken.setTokenizer(this.getClass().getName());
				newToken.setTrust(50);
				tokenSet.add(newToken);
			}
		}
	}

	private void tokenAssist(Set<SectionToken> tokenSet, EntityToken entity) {
		List<SectionToken> assists = entity.getAssists();
		if (CollectionUtils.isEmpty(assists)) {
			return;
		}
		List<SectionToken> assistTokens = new ArrayList<SectionToken>();
		for (SectionToken token : assists) {
			if (modelAssistKeySet.contains(token.getKey())) {
				assistTokens.add(token);
			}
		}

		for (SectionToken at : assistTokens) {
			Matcher matcher = MODEL_REG.matcher(at.getValue());
			while (matcher.find()) {
				SectionToken newToken = new SectionToken(at.getKey(), matcher.group());
				newToken.setParent(entity.getMaster());
				newToken.setTokenizer(this.getClass().getName());
				newToken.setTrust(100);
				tokenSet.add(newToken);
			}
		}

	}

}
