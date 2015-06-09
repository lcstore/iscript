package com.lezo.iscript.resulter.ident;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class BrandIdentifier implements Identifier {
	private static final String KEY_BRAND = "productBrand";

	@Override
	public void identify(IdentTarget target) {
		String sBrandField = target.getParams().get(KEY_BRAND);
		SynonymBrandService synonymBrandService = SpringBeanUtils.getBean(SynonymBrandService.class);
		if (StringUtils.isNotEmpty(sBrandField)) {
			Iterator<String> it = synonymBrandService.iteratorKeys();
			Set<String> hasSet = new HashSet<String>();
			while (it.hasNext()) {
				String sKey = it.next();
				if (sBrandField.contains(sKey)) {
					hasSet.add(sKey);
				}
			}
			//
		}
	}

}
