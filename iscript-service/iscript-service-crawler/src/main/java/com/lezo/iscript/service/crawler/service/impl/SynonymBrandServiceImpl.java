package com.lezo.iscript.service.crawler.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.BrandDao;
import com.lezo.iscript.service.crawler.dto.BrandDto;
import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.CharsUtils;

@Service
public class SynonymBrandServiceImpl implements SynonymBrandService {
	private static Logger logger = LoggerFactory.getLogger(SynonymBrandServiceImpl.class);

	private static class BrandInitor {
		private static final ConcurrentHashMap<String, Set<String>> brandSetMap = initBrandSetMap();

		private static ConcurrentHashMap<String, Set<String>> initBrandSetMap() {
			BrandServiceImpl brandService = new BrandServiceImpl();
			BrandDao brandDao = SpringBeanUtils.getBean(BrandDao.class);
			brandService.setBrandDao(brandDao);
			Long fromId = 0L;
			int limit = 500;
			long start = System.currentTimeMillis();
			Map<String, Set<String>> synCodeToBrandSetMap = new HashMap<String, Set<String>>();
			while (true) {
				long startMills = System.currentTimeMillis();
				List<BrandDto> dtoList = brandService.getBrandDtoFromId(fromId, limit);
				long costMills = System.currentTimeMillis() - startMills;
				logger.info("load brand dto,fromId:" + fromId + ",limit:" + limit + ",get size:" + dtoList.size()
						+ ",cost:" + costMills);
				for (BrandDto dto : dtoList) {
					Set<String> brandSet = synCodeToBrandSetMap.get(dto.getSynonymCode());
					if (brandSet == null) {
						brandSet = new HashSet<String>();
						synCodeToBrandSetMap.put(dto.getSynonymCode(), brandSet);
					}
					brandSet.add(CharsUtils.unifyChars(dto.getBrandName()));
				}
				if (dtoList.size() < limit) {
					break;
				}
				for (BrandDto dto : dtoList) {
					if (fromId < dto.getId()) {
						fromId = dto.getId();
					}
				}
			}
			ConcurrentHashMap<String, Set<String>> brandSetMap = new ConcurrentHashMap<String, Set<String>>();
			int count = 0;
			for (Entry<String, Set<String>> entry : synCodeToBrandSetMap.entrySet()) {
				Set<String> synBrandSet = entry.getValue();
				count += synBrandSet.size();
				Set<String> oSet = new HashSet<String>();
				for (String brand : synBrandSet) {
					Set<String> oldSet = brandSetMap.put(brand, synBrandSet);
					if (oldSet != null && !oldSet.isEmpty() && oldSet != synBrandSet) {
						logger.warn("diff synCode,but same name:" + brand + ",synCode:" + entry.getKey());
						oSet.addAll(oldSet);
					}
				}
				if (!oSet.isEmpty()) {
					synBrandSet.addAll(oSet);
				}
			}
			long cost = System.currentTimeMillis() - start;
			logger.info("init brand.synCode count:" + synCodeToBrandSetMap.size() + ",brandCount:" + count + ",cost:"
					+ cost);
			return brandSetMap;
		}
	}

	@Override
	public Set<String> getSynonyms(String brandName) {
		brandName = CharsUtils.unifyChars(brandName);
		return BrandInitor.brandSetMap.get(brandName);
	}

	@Override
	public Iterator<String> iteratorKeys() {
		return BrandInitor.brandSetMap.keySet().iterator();
	}

	@Override
	public boolean isSynonym(String left, String right) {
		if (StringUtils.isBlank(left) || StringUtils.isBlank(right)) {
			return false;
		}
		left = CharsUtils.unifyChars(left);
		right = CharsUtils.unifyChars(right);
		Set<String> brandSet = BrandInitor.brandSetMap.get(left);
		return brandSet == null ? false : brandSet.contains(right);
	}

}
