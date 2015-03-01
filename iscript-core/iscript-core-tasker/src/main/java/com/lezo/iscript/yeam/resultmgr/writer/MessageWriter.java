package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.service.crawler.service.MessageService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.utils.JSONUtils;

public class MessageWriter implements ObjectWriter<MessageDto> {
	private static Logger logger = LoggerFactory.getLogger(MessageWriter.class);
	private static final int MAX_MSG_LEN = 50;

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
			Map<String, List<MessageDto>> sameKeyMap = toSameKeyMap(entry.getValue());
			for (Entry<String, List<MessageDto>> sEntry : sameKeyMap.entrySet()) {
				List<MessageDto> sameList = sEntry.getValue();
				BatchIterator<MessageDto> it = new BatchIterator<MessageDto>(sameList, MAX_MSG_LEN);
				while (it.hasNext()) {
					List<MessageDto> batchList = it.next();
					JSONObject mObject = new JSONObject();
					JSONArray idArray = new JSONArray();
					for (MessageDto curDto : batchList) {
						JSONObject curObject = JSONUtils.getJSONObject(curDto.getMessage());
						Object tObject = JSONUtils.getObject(curObject, "tid");
						idArray.put(tObject);
					}
					MessageDto mergeDto = batchList.get(0);
					JSONObject oneObject = JSONUtils.getJSONObject(mergeDto.getMessage());
					String bid = unifyString(JSONUtils.getString(oneObject, "bid"), "-");
					JSONUtils.put(mObject, bid, idArray);
					mergeDto.setMessage(mObject.toString());
					mergeDto.setDataCount(batchList.size());
					mergeList.add(mergeDto);
				}
			}
		}
		SpringBeanUtils.getBean(MessageService.class).batchSaveDtos(mergeList);
	}

	private Map<String, List<MessageDto>> toSameKeyMap(List<MessageDto> dtoList) {
		Map<String, List<MessageDto>> sameKeyMap = new HashMap<String, List<MessageDto>>();
		for (MessageDto mDto : dtoList) {
			JSONObject mObject = JSONUtils.getJSONObject(mDto.getMessage());
			StringBuilder sb = new StringBuilder();
			sb.append(unifyString(mDto.getDataBucket(), ""));
			sb.append(".");
			sb.append(unifyString(mDto.getDataDomain(), ""));
			sb.append(".");
			sb.append(unifyString(JSONUtils.getString(mObject, "bid"), "-"));
			String key = sb.toString();
			List<MessageDto> sameList = sameKeyMap.get(key);
			if (sameList == null) {
				sameList = new ArrayList<MessageDto>();
				sameKeyMap.put(key, sameList);
			}
			sameList.add(mDto);
		}
		return sameKeyMap;
	}

	private String unifyString(String value, String defaultValue) {
		return StringUtils.isEmpty(value) ? defaultValue : value;
	}

}
