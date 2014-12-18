package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.BrandStoreDto;

public interface BrandStoreService extends BaseService<BrandStoreDto> {
	void saveBrandStoreDtoAndGetId(BrandStoreDto dto);

	List<BrandStoreDto> getBrandStoreDtoByCodes(List<String> brandCodeList, List<String> brandNameList, Integer siteId);

	List<BrandStoreDto> getBrandStoreDtoByIds(List<Long> idList);
}
