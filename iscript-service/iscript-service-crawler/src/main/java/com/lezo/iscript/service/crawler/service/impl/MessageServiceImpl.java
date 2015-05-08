package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.MessageDao;
import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.service.crawler.service.MessageService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class MessageServiceImpl implements MessageService {
	@Autowired
	private MessageDao messageDao;

	@Override
	public void batchInsertDtos(List<MessageDto> dtoList) {
		BatchIterator<MessageDto> it = new BatchIterator<MessageDto>(dtoList);
		while (it.hasNext()) {
			messageDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateDtos(List<MessageDto> dtoList) {
		BatchIterator<MessageDto> it = new BatchIterator<MessageDto>(dtoList);
		while (it.hasNext()) {
			messageDao.batchUpdate(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<MessageDto> dtoList) {
		batchInsertDtos(dtoList);
	}

	@Override
	public List<MessageDto> getMessageDtos(List<String> nameList, Integer status, Integer limit) {
		if (CollectionUtils.isEmpty(nameList) || (limit != null && limit < 1)) {
			return Collections.emptyList();
		}
		return messageDao.getMessageDtos(nameList, status, limit);
	}

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}

	@Override
	public void batchUpdateStatus(List<Long> idList, Integer status, String remark) {
		BatchIterator<Long> it = new BatchIterator<Long>(idList, 500);
		while (it.hasNext()) {
			messageDao.batchUpdateStatus(it.next(), status, remark);
		}
	}

	@Override
	public List<MessageDto> getEarlyMessageDtoByNameList(List<String> nameList, Integer status) {
		if (CollectionUtils.isEmpty(nameList)) {
			return Collections.emptyList();
		}
		List<MessageDto> earlyList = messageDao.getEarlyMessageByNameList(nameList, status);
		if (CollectionUtils.isEmpty(earlyList)) {
			return Collections.emptyList();
		}
		Set<Long> idSet = new HashSet<Long>();
		for (MessageDto mDto : earlyList) {
			idSet.add(mDto.getId());
		}
		List<Long> idList = new ArrayList<Long>(idSet);
		return messageDao.getMessageDtoByIdList(idList);
	}

	@Override
	public void updateStatusByCreateTime(List<String> nameList, String bucket, String domain, Date beforCreateTime,
			int fromStatus, int toStatus) {
		if (CollectionUtils.isEmpty(nameList)) {
			return;
		}
		messageDao.updateStatusByCreateTime(nameList, bucket, domain, beforCreateTime, fromStatus, toStatus);
	}
}
