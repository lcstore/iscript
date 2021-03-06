package com.lezo.iscript.service.crawler.service;

import java.util.Iterator;
import java.util.Set;

public interface SynonymBrandService {
	Set<String> getSynonyms(String brandName);

	boolean isSynonym(String left, String right);

	Iterator<String> iteratorKeys();
}
