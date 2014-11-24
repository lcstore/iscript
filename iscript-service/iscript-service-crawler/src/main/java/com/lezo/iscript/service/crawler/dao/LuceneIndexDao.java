package com.lezo.iscript.service.crawler.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.LuceneIndexDto;

public interface LuceneIndexDao extends BaseDao<LuceneIndexDto> {

	LuceneIndexDto getLatestLuceneIndexDto(@Param(value = "status") Integer status);

	LuceneIndexDto getLuceneIndexDtoByDay(@Param(value = "indexDay") Date indexDay);
}
