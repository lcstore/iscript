package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.ProductStandardDto;

public interface ProductStandardDao extends BaseDao<ProductStandardDto> {

	List<ProductStandardDto> getProductStandardDtoBySimilarCodes(@Param("similarCodeList") List<String> similarCodeList);

	List<ProductStandardDto> getProductStandardDtoByProductCodes(@Param("siteId") Integer siteId, @Param("codeList") List<String> codeList);

}
