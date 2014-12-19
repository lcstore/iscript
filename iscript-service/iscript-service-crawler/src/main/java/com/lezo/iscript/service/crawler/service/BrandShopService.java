package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.BrandShopDto;

public interface BrandShopService extends BaseService<BrandShopDto> {
	List<BrandShopDto> getBrandShopDtoByShopNameList(List<String> shopNameList, List<String> brandCodeList, Integer siteId);
}
