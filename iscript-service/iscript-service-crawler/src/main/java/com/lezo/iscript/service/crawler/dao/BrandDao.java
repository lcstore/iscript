package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.BrandDto;

public interface BrandDao extends BaseDao<BrandDto> {
	void insertBrandStoreDtoAndSetId(BrandDto dto);

	List<BrandDto> getBrandDtoByCodes(@Param("brandCodeList") List<String> brandCodeList,
			@Param("brandNameList") List<String> brandNameList, @Param("siteId") Integer siteId);

	List<BrandDto> getBrandDtoByIds(@Param("idList") List<Long> idList);

	List<String> getSynonymCodesByNameList(@Param("brandNameList") List<String> brandNameList);

	List<BrandDto> getBrandDtoBySynonymCodeList(@Param("synonymCodeList")List<String> synonymCodeList);

	List<BrandDto> getBrandDtoFromId(@Param("fromId")Long fromId,@Param("limit") int limit);
}
