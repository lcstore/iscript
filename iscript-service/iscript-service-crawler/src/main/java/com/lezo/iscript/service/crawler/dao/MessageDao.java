package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.MessageDto;

public interface MessageDao extends BaseDao<MessageDto> {

	List<MessageDto> getMessageDtos(@Param(value = "nameList") List<String> nameList,
			@Param(value = "status") Integer status, @Param(value = "limit") Integer limit);

	void batchUpdateStatus(@Param(value = "idList") List<Long> idList, @Param(value = "status") Integer status,
			@Param(value = "remark") String remark);
}
