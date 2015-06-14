package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.ProductStandardDto;

public interface ProductStandardDao extends BaseDao<ProductStandardDto> {

	List<ProductStandardDto> getDtoBySimilarCodes(@Param("similarCodeList") List<String> similarCodeList);

	List<ProductStandardDto> getDtoByProductCodes(@Param("siteId") Integer siteId,
			@Param("codeList") List<String> codeList);

	List<ProductStandardDto> geDtoByCategorysAndBrands(@Param("categorys") List<String> categorys,
			@Param("brands") List<String> brands);

}
