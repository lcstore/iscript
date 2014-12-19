package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.BrandDto;

public interface BrandService extends BaseService<BrandDto> {
	void saveBrandStoreDtoAndGetId(BrandDto dto);

	List<BrandDto> getBrandStoreDtoByCodes(List<String> brandCodeList, List<String> brandNameList, Integer siteId);

	List<BrandDto> getBrandStoreDtoByIds(List<Long> idList);
}
