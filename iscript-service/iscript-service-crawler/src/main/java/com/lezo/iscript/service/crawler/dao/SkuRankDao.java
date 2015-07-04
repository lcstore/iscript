package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.SkuRankDto;

public interface SkuRankDao extends BaseDao<SkuRankDto> {

	List<SkuRankDto> getDtoByCategoryOrBarnd(@Param("categroy") String categroy, @Param("brand") String brand);

	List<SkuRankDto> getDtoByMatchCodes(@Param("matchCodeList") List<Long> matchCodeList);
}
