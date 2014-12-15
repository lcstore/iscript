package com.lezo.iscript.service.crawler.dao;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.BrandShopDto;

public interface BrandShopDao extends BaseDao<BrandShopDto> {
	void insertBrandShopDtoAndSetId(BrandShopDto dto);
}
