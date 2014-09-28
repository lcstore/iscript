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
	private static final int MAX_MSG_LEN = 2000;

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
				key = key == null ? "-" : key;
				StringBuilder sb = bidMap.get(key);
				if (sb == null) {
					sb = new StringBuilder();
					bidMap.put(key, sb);
					typeDto = mDto;
				}
				Object tObject = JSONUtils.getObject(mObject, "tid");
				tObject = tObject == null ? "" : tObject;
				int mLen = sb.length() + tObject.toString().length() + key.length() + 10;
				if (mLen > MAX_MSG_LEN) {
					JSONObject newObject = new JSONObject();
					MessageDto cloneDto = newMessage(typeDto, newObject, key, sb);
					sb = new StringBuilder();
					bidMap.put(key, sb);
					sb.append(tObject);
					mergeList.add(cloneDto);
				} else {
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append(tObject);
				}
			}
			mergeMsg(mergeList, bidMap, typeDto);
		}
		SpringBeanUtils.getBean(MessageService.class).batchSaveDtos(mergeList);
	}

	private void mergeMsg(List<MessageDto> mergeList, Map<String, StringBuilder> bidMap, MessageDto typeDto) {
		JSONObject newObject = new JSONObject();
		int len = 0;
		for (Entry<String, StringBuilder> bEntry : bidMap.entrySet()) {
			String msg = bEntry.getValue().toString();
			int addNew = msg.length() + bEntry.getKey().length() + 10;
			len += addNew;
			if (len > MAX_MSG_LEN) {
				MessageDto cloneDto = typeDto.clone();
				cloneDto.setMessage(newObject.toString());
				mergeList.add(cloneDto);
				newObject = new JSONObject();
				len = 0;
			} else {
				JSONArray mArray = null;
				try {
					mArray = newArrayObject(msg);
				} catch (JSONException e) {
					logger.warn(String.format("type:%s,bid:%s,idArray:%s", typeDto.getName(), bEntry.getKey(), msg), e);
				}
				JSONUtils.put(newObject, bEntry.getKey(), mArray);
			}

		}
		if (len > 0) {
			MessageDto cloneDto = typeDto.clone();
			cloneDto.setMessage(newObject.toString());
			mergeList.add(cloneDto);
		}
	}

	private MessageDto newMessage(MessageDto typeDto, JSONObject mObject, String key, StringBuilder sb) {
		JSONArray mArray = null;
		try {
			mArray = newArrayObject(sb.toString());
		} catch (JSONException e) {
			logger.warn(String.format("type:%s,bid:%s,idArray:%s", typeDto.getName(), key, sb), e);
		}
		JSONUtils.put(mObject, key, mArray);
		MessageDto cloneDto = typeDto.clone();
		cloneDto.setMessage(mObject.toString());
		return cloneDto;
	}

	private JSONArray newArrayObject(String data) throws JSONException {
		return new JSONArray("[" + data + "]");
	}

	@Override
	public void flush() {

	}

}
