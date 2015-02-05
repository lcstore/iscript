package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.ProxyCollectHisDto;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年2月5日
 */
public interface ProxyCollectHisDao extends BaseDao<ProxyCollectHisDto> {

	List<ProxyCollectHisDto> getProxyCollectHisDtoByFromId(@Param("fromId") Long fromId, @Param("limit") int limit);
}
