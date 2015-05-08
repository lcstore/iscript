package com.lezo.iscript.service.crawler.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.MessageDto;

public interface MessageDao extends BaseDao<MessageDto> {

	List<MessageDto> getMessageDtos(@Param(value = "nameList") List<String> nameList,
			@Param(value = "status") Integer status, @Param(value = "limit") Integer limit);

	void batchUpdateStatus(@Param(value = "idList") List<Long> idList, @Param(value = "status") Integer status,
			@Param(value = "remark") String remark);

	@MapKey("NAME")
	List<MessageDto> getEarlyMessageByNameList(@Param(value = "nameList") List<String> nameList,
			@Param(value = "status") Integer status);

	List<MessageDto> getMessageDtoByIdList(@Param(value = "idList") List<Long> idList);

	void updateStatusByCreateTime(@Param("nameList") List<String> nameList, @Param("bucket") String bucket,
			@Param("domain") String domain, @Param("beforCreateTime") Date beforCreateTime,
			@Param("fromStatus") int fromStatus, @Param("toStatus") int toStatus);
}
