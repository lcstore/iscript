package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.BrandRepoDto;

public interface BrandRepoDao extends BaseDao<BrandRepoDto> {

    List<BrandRepoDto> getDtoByCoreOrSort(@Param("coreList") List<String> coreList, @Param("sortName") String sortName);

    List<BrandRepoDto> getDtoByIdWithLimit(@Param("fromId") long fromId, @Param("limit") int limit);

}
