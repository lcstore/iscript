package com.lezo.iscript.service.crawler.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.MatchDto;

public interface MatchService extends BaseService<MatchDto> {
	List<MatchDto> getDtoByIds(List<Long> idList);

	List<MatchDto> getMatchDtoByMatchCodes(@Param("matchCodeList") List<Long> matchCodeList);

	List<MatchDto> getMatchDtoByProductCodes(@Param("siteId") Integer siteId, @Param("codeList") List<String> codeList);

}
