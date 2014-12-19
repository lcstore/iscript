package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.BrandShopDto;

public interface BrandShopDao extends BaseDao<BrandShopDto> {
	List<BrandShopDto> getBrandShopDtoByShopNameList(@Param("shopNameList") List<String> shopNameList, @Param("brandCodeList") List<String> brandCodeList, @Param("siteId") Integer siteId);
}
