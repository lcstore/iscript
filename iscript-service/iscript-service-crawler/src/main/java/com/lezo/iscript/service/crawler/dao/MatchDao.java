package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.MatchDto;

public interface MatchDao extends BaseDao<MatchDto> {

	List<MatchDto> getMatchDtoByIds(@Param("idList") List<Long> idList);
	
	List<MatchDto> getMatchDtoBySimilarCodes(@Param("similarCodeList") List<String> similarCodeList);

	List<MatchDto> getMatchDtoByProductCodes(@Param("siteId") Integer siteId, @Param("codeList") List<String> codeList);

}
