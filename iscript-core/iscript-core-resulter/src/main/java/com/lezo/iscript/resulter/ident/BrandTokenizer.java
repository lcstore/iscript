package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.service.crawler.service.SynonymBrandService;

public class BrandTokenizer extends AbstractTokenizer {
	private static final Set<String> brandAssistKeySet = new HashSet<String>();
	static {
		brandAssistKeySet.add("productBrand");
		brandAssistKeySet.add("品牌");
		brandAssistKeySet.add("商品品牌");
	}
	private SynonymBrandService synonymBrandService;

	public BrandTokenizer(SynonymBrandService synonymBrandService) {
		super();
		this.synonymBrandService = synonymBrandService;
	}

	@Override
	protected void doToken(List<EntityToken> entityTokens) {

		for (EntityToken entity : entityTokens) {
			List<SectionToken> tokenList = doToken(entity);
			for (SectionToken token : tokenList) {
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
		entity.getLeveChildren(leaveList, entity.getMaster());
		Iterator<String> it = synonymBrandService.iteratorKeys();
		while (it.hasNext()) {
			String token = it.next();
			for (SectionToken sectionToken : leaveList) {
				if (contains(sectionToken.getValue(), token)) {
					SectionToken newToken = new SectionToken(sectionToken.getKey(), token);
					newToken.setParent(sectionToken);
					newToken.setTokenizer(this.getClass().getName());
					newToken.setTrust(50);
					tokenSet.add(newToken);
				}
			}
		}
	}

	private void tokenAssist(Set<SectionToken> tokenSet, EntityToken entity) {
		List<SectionToken> assists = entity.getAssists();
		if (CollectionUtils.isEmpty(assists)) {
			return;
		}
		List<SectionToken> brandTokens = new ArrayList<SectionToken>();
		for (SectionToken token : assists) {
			if (brandAssistKeySet.contains(token.getKey())) {
				brandTokens.add(token);
			}
		}
		Iterator<String> it = synonymBrandService.iteratorKeys();
		while (it.hasNext()) {
			String token = it.next();
			for (SectionToken sectionToken : brandTokens) {
				if (contains(sectionToken.getValue(), token)) {
					SectionToken newToken = new SectionToken(sectionToken.getKey(), token);
					newToken.setParent(entity.getMaster());
					newToken.setTokenizer(this.getClass().getName());
					newToken.setTrust(100);
					tokenSet.add(newToken);
				}
			}
		}

	}

}
