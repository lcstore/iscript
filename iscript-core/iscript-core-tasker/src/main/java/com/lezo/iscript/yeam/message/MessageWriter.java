package com.lezo.iscript.yeam.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONObject;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.service.crawler.service.MessageService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.utils.JSONUtils;

public class MessageWriter implements ObjectWriter<MessageDto> {
   
	@Override
	public void write(List<MessageDto> dataList) {
		if(CollectionUtils.isEmpty(dataList)){
			return;
		}
		Map<String, List<MessageDto>> typeMap = new HashMap<String, List<MessageDto>>();
		for (MessageDto data : dataList) {
			List<MessageDto> dtoList = typeMap.get(data.getName());
			if (dtoList == null) {
				dtoList = new ArrayList<MessageDto>();
				typeMap.put(data.getName(), dtoList);
			}
			dtoList.add(data);
		}
		List<MessageDto> mergeList = new ArrayList<MessageDto>(typeMap.size());
		for (Entry<String, List<MessageDto>> entry : typeMap.entrySet()) {
			BatchIterator<MessageDto> it = new BatchIterator<MessageDto>(entry.getValue(),50);
			while(it.hasNext()){
				MessageDto mergeDto = mergeMessage(entry.getKey(), it.next());
				mergeList.add(mergeDto);
			}
		}
		SpringBeanUtils.getBean(MessageService.class).batchSaveDtos(mergeList);
	}

	private MessageDto mergeMessage(String key, List<MessageDto> messageList) {
		MessageDto messageDto = messageList.get(0);
		int size = messageList.size();
		JSONObject mObject = new JSONObject();
		JSONUtils.put(mObject, "0", messageDto.getMessage());
		for (int i = 1; i < size; i++) {
			JSONUtils.put(mObject, "" + i, messageList.get(i).getMessage());
		}
		messageDto.setMessage(mObject.toString());
		return messageDto;
	}

	@Override
	public void flush() {

	}

}
