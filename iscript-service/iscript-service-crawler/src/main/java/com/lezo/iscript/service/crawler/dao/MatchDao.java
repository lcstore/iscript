package com.lezo.iscript.service.crawler.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.common.Batch;
import com.lezo.iscript.service.crawler.dto.MatchDto;

public interface MatchDao extends BaseDao<MatchDto> {

    List<MatchDto> getDtoBySkuCodes(@Param("skuCodes") List<String> skuCodes, @Param("isDelete") Integer isDelete);

    List<String> getMatchCodeWithBlankItemCode();

    List<MatchDto> getDtoByMatchCodes(@Param("mCodes") List<String> mCodes, @Param("isDelete") Integer isDelete);

    int updateItemCodeByMatchCode(@Param("mCode") String mCode, @Param("itemCode") String itemCode);

    List<MatchDto> getDtoByMatchCodesWithLimit(@Param("mCodes") List<String> mCodes, @Param("offset") int offset,
            @Param("limit") int limit);

    List<MatchDto> getDtoBySiteIdWithCreateDate(@Param("siteId") int siteId,
            @Param("fromCreateDate") Date fromCreateDate,
            @Param("toCreateDate") Date toCreateDate, @Param("fromId") long fromId,
            @Param("limit") int limit);

    void batchUpdateDtoBySkuCode(@Batch List<MatchDto> dtoList);

    List<MatchDto> getDtoByBarCodes(@Param("barCodes") List<String> barCodes, @Param("isDelete") Integer isDelete);

}
