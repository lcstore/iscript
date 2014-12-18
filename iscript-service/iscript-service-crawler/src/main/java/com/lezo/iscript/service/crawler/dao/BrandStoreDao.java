package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.BrandStoreDto;

public interface BrandStoreDao extends BaseDao<BrandStoreDto> {
	void insertBrandStoreDtoAndSetId(BrandStoreDto dto);

	List<BrandStoreDto> getBrandStoreDtoByCodes(@Param("brandCodeList")List<String> brandCodeList,@Param("brandNameList") List<String> brandNameList, @Param("siteId")Integer siteId);

	List<BrandStoreDto> getBrandStoreDtoByIds(@Param("idList")List<Long> idList);
}
