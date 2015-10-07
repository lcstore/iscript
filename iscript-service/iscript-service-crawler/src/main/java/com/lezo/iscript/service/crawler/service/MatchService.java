package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.MatchDto;

public interface MatchService extends BaseService<MatchDto> {
    List<MatchDto> getDtoByIds(List<Long> idList);

    List<MatchDto> getDtoBySkuCodes(List<String> skuCodes, Integer isDelete);

    List<String> getMatchCodeWithNullItemCode();

    int updateItemCodeByMatchCode(String matchCode, String itemCode);

    List<MatchDto> getDtoByMatchCodes(List<String> mCodes, Integer isDelete);

    List<MatchDto> getDtoByMatchCodesWithLimit(List<String> mCodes, int offset, int limit);
}
