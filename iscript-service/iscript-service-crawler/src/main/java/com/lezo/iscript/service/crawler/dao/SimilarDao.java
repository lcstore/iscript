package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.SimilarDto;

public interface SimilarDao extends BaseDao<SimilarDto> {
	List<SimilarDto> getSimilarDtoByProductCodes(@Param("siteId") Integer siteId,
			@Param("codeList") List<String> codeList);

	List<SimilarDto> getSimilarDtoBySimilarCodes(@Param("similarCodeList") List<Long> similarCodeList);
}
