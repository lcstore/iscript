package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.utils.UnifyCharsUtils;

public abstract class AbstractTokenizer implements Tokenizer {

	@Override
	public void identify(List<EntityToken> entityList) {
		if (CollectionUtils.isEmpty(entityList)) {
			return;
		}
		division(entityList);
		doToken(entityList);
		doTrust(entityList);
	}

	protected void division(List<EntityToken> entityList) {
	}

	protected abstract void doToken(List<EntityToken> entityList);

	protected void doTrust(List<EntityToken> entityList) {
		for (EntityToken entity : entityList) {
			List<SectionToken> leaveList = new ArrayList<SectionToken>();
			entity.getLeveChildren(leaveList, entity.getMaster());
			List<SectionToken> brandTokens = entity.getTokensByTokenizer(leaveList, this.getClass().getName());
			Map<String, List<SectionToken>> value2TokensMap = new HashMap<String, List<SectionToken>>();
			for (SectionToken bToken : brandTokens) {
				List<SectionToken> tokenList = value2TokensMap.get(bToken.getValue());
				if (tokenList == null) {
					tokenList = new ArrayList<SectionToken>();
					value2TokensMap.put(bToken.getValue(), tokenList);
				}
				tokenList.add(bToken);
			}
			for (Entry<String, List<SectionToken>> entry : value2TokensMap.entrySet()) {
				int trustSum = 0;
				for (SectionToken token : entry.getValue()) {
					trustSum += token.getTrust();
				}
				for (SectionToken token : entry.getValue()) {
					token.setTrust(trustSum);
				}
			}
		}
	}

	protected boolean contains(String source, String token) {
		return StringUtils.isNotBlank(token) && StringUtils.isNotBlank(source)
				&& UnifyCharsUtils.unifyChars(source).contains(token);
	}

}
