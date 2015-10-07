package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.SimilarJobDto;

public interface SimilarJobDao extends BaseDao<SimilarJobDto> {

    List<SimilarJobDto> getDtoByStatus(@Param("fromId") Long fromId, @Param("status") int status,
            @Param("limit") int limit);

}
