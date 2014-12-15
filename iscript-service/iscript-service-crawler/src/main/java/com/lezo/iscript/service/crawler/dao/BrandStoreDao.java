package com.lezo.iscript.service.crawler.dao;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.BrandStoreDto;

public interface BrandStoreDao extends BaseDao<BrandStoreDto> {
	void insertBrandStoreDtoAndSetId(BrandStoreDto dto);
}
