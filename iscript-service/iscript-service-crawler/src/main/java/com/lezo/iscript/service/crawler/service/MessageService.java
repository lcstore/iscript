package com.lezo.iscript.service.crawler.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.MessageDto;

public interface MessageService extends BaseService<MessageDto> {
	List<MessageDto> getMessageDtos(List<String> nameList, Integer status, Integer limit);

	void batchUpdateStatus(List<Long> idList, Integer status, String remark);

	List<MessageDto> getEarlyMessageDtoByNameList(List<String> nameList, Integer status);

	void updateStatusByCreateTime(List<String> nameList, String bucket, String domain, Date beforCreateTime,
			int fromStatus, int toStatus);
}
