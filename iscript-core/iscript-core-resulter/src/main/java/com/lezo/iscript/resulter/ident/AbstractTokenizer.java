package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.utils.CharsUtils;

public abstract class AbstractTokenizer implements Tokenizer {
	private static final String DIVISION_MARK = "_@DIVISION@_";

	@Override
	public void identify(List<EntityToken> entityList) {
		if (CollectionUtils.isEmpty(entityList)) {
			return;
		}
		doToken(entityList);
		division(entityList);
		doTrust(entityList);
	}

	protected abstract void doToken(List<EntityToken> entityList);

	protected void division(List<EntityToken> entityList) {
		for (EntityToken entity : entityList) {
			List<SectionToken> leaveList = new ArrayList<SectionToken>();
			EntityToken.getLeveChildren(leaveList, entity.getMaster());
			List<SectionToken> tokenList = EntityToken.getTokensByTokenizer(leaveList, this.getClass().getName());
			if (tokenList == null) {
				continue;
			}
			EntityToken.sortTokenLengthDesc(tokenList);
			String mString = entity.getMaster().getValue();
			for (SectionToken token : tokenList) {
				mString = mString.replace(token.getValue(), DIVISION_MARK);
			}
			String[] splitArrays = mString.split(DIVISION_MARK);
			handleDivision(splitArrays, entity.getMaster());
		}
	}

	private void handleDivision(String[] splitArrays, SectionToken master) {
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

	protected void doTrust(List<EntityToken> entityList) {
		for (EntityToken entity : entityList) {
			List<SectionToken> leaveList = new ArrayList<SectionToken>();
			EntityToken.getLeveChildren(leaveList, entity.getMaster());
			List<SectionToken> brandTokens = EntityToken.getTokensByTokenizer(leaveList, this.getClass().getName());
			Map<String, List<SectionToken>> value2TokensMap = new HashMap<String, List<SectionToken>>();
			for (SectionToken bToken : brandTokens) {
				String chars = CharsUtils.unifyChars(bToken.getValue());
				List<SectionToken> tokenList = value2TokensMap.get(chars);
				if (tokenList == null) {
					tokenList = new ArrayList<SectionToken>();
					value2TokensMap.put(chars, tokenList);
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

}
