package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.service.crawler.service.SynonymBrandService;

public class BrandIdentifierTest {

	@Test
	public void testIdentify() {
		BrandIdentifier identifier = new BrandIdentifier();
		List<String> brandList = new ArrayList<String>();
		brandList.add("三星");
		brandList.add("samsung");
		Collections.sort(brandList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		identifier.setSynonymBrandService(makeSynonymBrandService(brandList));
		IdentSource source = new IdentSource();
		IdentTarget target = new IdentTarget();
		source.setProductName("三星 Galaxy S5 (G9008W) 闪耀白 移动4G手机 双卡双待");
		identifier.identify(source, target);
		Set<IdentToken> tokenSet = target.getTargets().get(BrandIdentifier.class.getSimpleName());
		List<IdentToken> tokenList = new ArrayList<IdentToken>(tokenSet);
		Assert.assertEquals(tokenList.size(), 1);
		Assert.assertEquals("三星", tokenList.get(0).getToken());
	}

	@Test
	public void testIdentify_ProductBrand() {
		BrandIdentifier identifier = new BrandIdentifier();
		List<String> brandList = new ArrayList<String>();
		brandList.add("三星");
		brandList.add("samsung");
		identifier.setSynonymBrandService(makeSynonymBrandService(brandList));
		IdentSource source = new IdentSource();
		IdentTarget target = new IdentTarget();
		source.setProductName("三星 Galaxy S5 (G9008W) 闪耀白 移动4G手机 双卡双待");
		source.setProductBrand("三星（SAMSUNG）");
		identifier.identify(source, target);
		Set<IdentToken> tokenSet = target.getTargets().get(BrandIdentifier.class.getSimpleName());
		List<IdentToken> tokenList = new ArrayList<IdentToken>(tokenSet);
		Assert.assertEquals(tokenList.size(), 2);
		Assert.assertEquals("三星", tokenList.get(0).getToken());
	}

	private SynonymBrandService makeSynonymBrandService(final List<String> brandList) {
		return new SynonymBrandService() {

			@Override
			public Set<String> getSynonyms(String brandName) {
				return new HashSet<String>(brandList);
			}

			@Override
			public boolean isSynonym(String left, String right) {
				return false;
			}

			@Override
			public Iterator<String> iteratorKeys() {
				return brandList.iterator();
			}
		};
	}
}
