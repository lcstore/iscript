package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.SkuRankDto;

public interface SkuRankService extends BaseService<SkuRankDto> {

	List<SkuRankDto> getDtoByIds(List<Long> idList);

	List<SkuRankDto> getDtoByCategoryOrBarnd(String categroy, String brand);

	List<SkuRankDto> getDtoByMatchCodes(List<Long> matchCodeList);
}
