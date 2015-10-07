package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.SiteDto;

public interface SiteDao extends BaseDao<SiteDto> {

    List<SiteDto> getSiteDtoByLevel(@Param("level") Integer level);
}
