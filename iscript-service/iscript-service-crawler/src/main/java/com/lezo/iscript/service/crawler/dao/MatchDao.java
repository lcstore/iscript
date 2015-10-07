package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.MatchDto;

public interface MatchDao extends BaseDao<MatchDto> {

    List<MatchDto> getDtoBySkuCodes(@Param("skuCodes") List<String> skuCodes, @Param("isDelete") Integer isDelete);

    List<String> getMatchCodeWithBlankItemCode();

    List<MatchDto> getDtoByMatchCodes(@Param("mCodes") List<String> mCodes, @Param("isDelete") Integer isDelete);

    int updateItemCodeByMatchCode(@Param("mCode") String mCode, @Param("itemCode") String itemCode);

    List<MatchDto> getDtoByMatchCodesWithLimit(@Param("mCodes") List<String> mCodes, @Param("offset") int offset,
            @Param("limit") int limit);

}
