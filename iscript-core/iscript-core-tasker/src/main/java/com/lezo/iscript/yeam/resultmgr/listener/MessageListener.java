package com.lezo.iscript.yeam.resultmgr.listener;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class MessageListener implements IResultListener {
	private static Logger logger = LoggerFactory.getLogger(MessageListener.class);

	@Override
	public void handle(ResultWritable result) {
		if ("FileMessage" != result.getType()) {
			return;
		}
		MessageDto messageDto = createMessage(result);
	}

	private MessageDto createMessage(ResultWritable result) {
		MessageDto dto = new MessageDto();
		dto.setType(result.getType());
		dto.setSource("tasker");
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());
		dto.setStatus(MessageDto.NEW_MESSGE);
		dto.setMessage(result.getResult());
		return dto;
	}
}
