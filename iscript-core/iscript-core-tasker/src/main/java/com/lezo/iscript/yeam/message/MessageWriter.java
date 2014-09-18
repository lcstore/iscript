package com.lezo.iscript.yeam.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.service.crawler.service.MessageService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;

public class MessageWriter implements ObjectWriter<MessageDto> {
	private static Logger logger = LoggerFactory.getLogger(MessageWriter.class);

	@Override
	public void write(List<MessageDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
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
		List<MessageDto> mergeList = new ArrayList<MessageDto>(dataList.size() >> 1);
		for (Entry<String, List<MessageDto>> entry : typeMap.entrySet()) {
			Map<String, StringBuilder> bidMap = new HashMap<String, StringBuilder>(entry.getValue().size() >> 1);
			MessageDto typeDto = null;
			for (MessageDto mDto : entry.getValue()) {
				JSONObject mObject = JSONUtils.getJSONObject(mDto.getMessage());
				String key = JSONUtils.getString(mObject, "bid");
				key = key == null ? "" : key;
				StringBuilder sb = bidMap.get(key);
				if (sb == null) {
					sb = new StringBuilder();
					bidMap.put(key, sb);
					typeDto = mDto;
				}
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(JSONUtils.getObject(mObject, "tid"));
			}
			JSONObject newObject = new JSONObject();
			int len = 0;
			for (Entry<String, StringBuilder> bEntry : bidMap.entrySet()) {
				String msg = bEntry.getValue().toString();
				try {
					JSONUtils.put(newObject, bEntry.getKey(), new JSONArray("[" + msg + "]"));
					len += msg.length();
				} catch (JSONException e) {
					logger.warn(String.format("type:%s,bid:%s,idArray:%s", entry.getKey(), bEntry.getKey(), msg), e);
				}
				if (len >= 1200) {
					MessageDto cloneDto = typeDto.clone();
					cloneDto.setMessage(newObject.toString());
					mergeList.add(cloneDto);
					newObject = new JSONObject();
					len = 0;
				}
			}
			if (len > 0) {
				MessageDto cloneDto = typeDto.clone();
				cloneDto.setMessage(newObject.toString());
				mergeList.add(cloneDto);
			}
		}
		SpringBeanUtils.getBean(MessageService.class).batchSaveDtos(mergeList);
	}

	@Override
	public void flush() {

	}

}
