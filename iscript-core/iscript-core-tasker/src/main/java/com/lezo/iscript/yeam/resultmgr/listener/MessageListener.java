package com.lezo.iscript.yeam.resultmgr.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.writer.WriteNotifyer;
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
		JSONUtils.put(mObject, "tid", result.getTaskId());
		JSONUtils.put(mObject, "bid", JSONUtils.getString(argsObject, "bid"));
		messageDto.setMessage(mObject.toString());
		messageDto.setDataBucket(JSONUtils.getString(argsObject, "data_bucket"));
		messageDto.setDataDomain(JSONUtils.getString(argsObject, "data_domain"));
		messageDto.setDataCount(1);
		if (StringUtils.isEmpty(messageDto.getDataBucket())) {
			messageDto.setDataBucket("");
		}
		if (StringUtils.isEmpty(messageDto.getDataDomain())) {
			messageDto.setDataDomain("");
		}
		List<Object> dataList = new ArrayList<Object>(1);
		dataList.add(messageDto);
		WriteNotifyer.getInstance().doNotify(dataList);
	}

	private MessageDto createMessage(ResultWritable result) {
		MessageDto dto = new MessageDto();
		dto.setName(result.getType());
		dto.setSource("tasker");
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());
		dto.setStatus(MessageDto.STATUS_NEW);
		dto.setSortCode(0);
		return dto;
	}
}
