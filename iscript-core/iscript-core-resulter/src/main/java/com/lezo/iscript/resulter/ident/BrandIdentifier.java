package com.lezo.iscript.resulter.ident;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lombok.Data;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.utils.UnifyCharsUtils;

@Data
public class BrandIdentifier implements Identifier {
	private SynonymBrandService synonymBrandService;

	public BrandIdentifier() {
	}

	public BrandIdentifier(SynonymBrandService synonymBrandService) {
		super();
		this.synonymBrandService = synonymBrandService;
	}

	@Override
	public void identify(IdentSource source, IdentTarget target) {
		IdentSource param = source;
		String clsName = this.getClass().getSimpleName();
		if (isTokenTrusty(param)) {
			IdentToken identToken = createIdentToken(param.getTokenBrand());
			identToken.setSource(IdentToken.SOURCE_RANGE);
			identToken.setSynonyms(synonymBrandService.getSynonyms(identToken.getToken()));
			Set<IdentToken> tokenSet = new HashSet<IdentToken>();
			target.getTargets().put(clsName, tokenSet);
			tokenSet.add(identToken);
		} else {
			Set<IdentToken> tokenSet = new HashSet<IdentToken>();
			target.getTargets().put(clsName, tokenSet);
			Iterator<String> it = synonymBrandService.iteratorKeys();
			while (it.hasNext()) {
				String token = it.next();
				if (contains(param.getProductBrand(), token)) {
					IdentToken identToken = createIdentToken(token);
					identToken.setSynonyms(synonymBrandService.getSynonyms(identToken.getToken()));
					identToken.setSource(IdentToken.SOURCE_RANGE);
					tokenSet.add(identToken);
				} else if (contains(param.getProductName(), token)) {
					IdentToken identToken = createIdentToken(token);
					identToken.setSynonyms(synonymBrandService.getSynonyms(identToken.getToken()));
					tokenSet.add(identToken);
				}
			}
		}

	}

	private IdentToken createIdentToken(String token) {
		IdentToken identToken = new IdentToken();
		identToken.setToken(token);
		identToken.setIdentifier(this);
		return identToken;
	}

	private boolean contains(String source, String token) {
		return StringUtils.isNotBlank(token) && StringUtils.isNotBlank(source)
				&& UnifyCharsUtils.unifyChars(source).contains(token);
	}

	private boolean isTokenTrusty(IdentSource param) {
		String token = param.getTokenBrand();
		return contains(param.getProductBrand(), token) && contains(param.getProductName(), token);
	}

}
