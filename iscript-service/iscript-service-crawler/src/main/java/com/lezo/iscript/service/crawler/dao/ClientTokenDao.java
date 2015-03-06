package com.lezo.iscript.service.crawler.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.ClientTokenDto;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年3月6日
 */
public interface ClientTokenDao extends BaseDao<ClientTokenDto> {

	List<ClientTokenDto> getClientTokenDtoByRefreshDate(@Param("fromRefreshDate") Date fromRefreshDate, @Param("toRefreshDate") Date toRefreshDate, @Param("clientType") String clientType);

	List<ClientTokenDto> getClientTokenDtoByUpdateTime(@Param("afterTime") Date afterTime);
}
