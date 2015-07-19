package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.MatchDto;

public interface MatchDao extends BaseDao<MatchDto> {

	List<MatchDto> getMatchDtoByMatchCodes(@Param("matchCodeList") List<Long> matchCodeList);

	List<MatchDto> getMatchDtoByProductCodes(@Param("siteId") Integer siteId, @Param("codeList") List<String> codeList);

}
