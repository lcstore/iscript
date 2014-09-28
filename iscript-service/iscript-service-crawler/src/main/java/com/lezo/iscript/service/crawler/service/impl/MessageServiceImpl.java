package com.lezo.iscript.service.crawler.service.impl;

import java.util.Collections;
import java.util.List;

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

}
