package com.lezo.iscript.yeam.resultmgr.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.message.MessageCacher;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class MessageListener implements IResultListener {
	private static Logger logger = LoggerFactory.getLogger(MessageListener.class);

	@Override
	public void handle(ResultWritable result) {
		if (ResultWritable.RESULT_SUCCESS != result.getStatus()) {
			return;
		}
		MessageDto messageDto = createMessage(result);
		JSONObject gObject = JSONUtils.getJSONObject(result.getResult());
		JSONObject argsObject = JSONUtils.get(gObject, "args");
		JSONObject mObject = new JSONObject();
		JSONUtils.put(mObject, "bid", JSONUtils.getString(argsObject, "bid"));
		JSONUtils.put(mObject, "tid", result.getTaskId());
		messageDto.setMessage(mObject.toString());
		List<MessageDto> dataList = new ArrayList<MessageDto>(1);
		dataList.add(messageDto);
		MessageCacher.getInstance().getBufferWriter().write(dataList);
	}

	private MessageDto createMessage(ResultWritable result) {
		MessageDto dto = new MessageDto();
		dto.setName(result.getType());
		dto.setSource("tasker");
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());
		dto.setStatus(MessageDto.NEW_MESSGE);
		dto.setSortCode(0);
		return dto;
	}
}
