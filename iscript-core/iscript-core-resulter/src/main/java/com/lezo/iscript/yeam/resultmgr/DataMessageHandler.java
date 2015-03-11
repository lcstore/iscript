package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.service.crawler.service.MessageService;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.directory.DirectoryDescriptor;
import com.lezo.iscript.yeam.resultmgr.directory.DirectoryEventListener;

public class DataMessageHandler {
	private static final String DIR_SEPARATOR = "/";
	private static Logger logger = LoggerFactory.getLogger(DataMessageHandler.class);
	private static AtomicBoolean running = new AtomicBoolean(false);
	private static DirectoryEventListener directoryEventListener = new DirectoryEventListener();
	@Autowired
	private MessageService messageService;
	private List<String> nameList;
	private Integer limit = 10;

	public void run() {
		if (running.get()) {
			logger.warn("DataMessageHandler is running..");
			return;
		}
		long start = System.currentTimeMillis();
		int query = 0;
		try {
			logger.info("start to do DataMessageHandler ..");
			running.set(true);
			List<MessageDto> dtoList = messageService.getMessageDtos(nameList, 0, limit);
			logger.info("Query.name:{},limit:{},size:{}.", nameList, limit, dtoList.size());
			Set<Long> idSet = new HashSet<Long>(dtoList.size());
			Map<String, DirectoryDescriptor> keyDirectoryMap = new HashMap<String, DirectoryDescriptor>(getCapacity(dtoList.size()));
			Set<Long> emptyHostSet = new HashSet<Long>(dtoList.size());
			for (MessageDto dto : dtoList) {
				if (StringUtils.isEmpty(dto.getDataBucket()) || StringUtils.isEmpty(dto.getDataDomain())) {
					emptyHostSet.add(dto.getId());
					continue;
				}
				idSet.add(dto.getId());
				JSONObject mObject = JSONUtils.getJSONObject(dto.getMessage());
				if (mObject == null) {
					continue;
				}
				String dateString = getDateString(dto.getCreateTime());
				Iterator<?> it = mObject.keys();
				String host = dto.getDataBucket().trim() + "." + dto.getDataDomain().trim();
				while (it.hasNext()) {
					StringBuilder sb = new StringBuilder("iscript/");
					sb.append(dateString);
					sb.append(DIR_SEPARATOR);
					sb.append(dto.getName());
					sb.append(DIR_SEPARATOR);
					sb.append(it.next().toString());
					String path = sb.toString();
					String key = host + ":" + path;
					DirectoryDescriptor descriptor = keyDirectoryMap.get(key);
					if (descriptor == null) {
						descriptor = new DirectoryDescriptor(path, dto.getDataBucket().trim(), dto.getDataDomain().trim());
						descriptor.setCreateTime(dto.getCreateTime());
						keyDirectoryMap.put(key, descriptor);
					} else if (descriptor.getCreateTime().after(dto.getCreateTime())) {
						descriptor.setCreateTime(dto.getCreateTime());
					}
				}
			}
			logger.info("directory.count:" + keyDirectoryMap.size() + ",affectCount:" + idSet.size() + ",totalCount:" + dtoList.size());
			messageService.batchUpdateStatus(new ArrayList<Long>(idSet), -1, "size:" + idSet.size());
			messageService.batchUpdateStatus(new ArrayList<Long>(emptyHostSet), -2, "emptyHost.size:" + emptyHostSet.size());
			buildeProducer(keyDirectoryMap);
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("Finish to handle.name:%s,limit:%s,query:%s,dir:%s,cost:%s.", nameList, limit, query, keyDirectoryMap.size(), cost);
			logger.info(msg);
		} catch (Exception e) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("Finish to handle.name:%s,limit:%s,query:%s,cost:%s.Cause:", nameList, limit, query, cost);
			logger.warn(msg, e);
		} finally {
			running.set(false);
		}
	}

	private int getCapacity(int initialCapacity) {
		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity) {
			capacity <<= 1;
		}
		// ensure not to call resize()
		int threshold = (int) (initialCapacity * 0.75);
		while (capacity < threshold) {
			capacity <<= 1;
		}
		return capacity;
	}

	private void buildeProducer(Map<String, DirectoryDescriptor> keyDirectoryMap) {
		for (Entry<String, DirectoryDescriptor> entry : keyDirectoryMap.entrySet()) {
			directoryEventListener.fireEvent(entry.getValue());
		}
	}

	private String getDateString(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int days = c.get(Calendar.DAY_OF_MONTH);
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append(addHeadZero(month));
		sb.append(addHeadZero(days));
		return sb.toString();
	}

	private String addHeadZero(int data) {
		return data < 10 ? "0" + data : "" + data;
	}

	public List<String> getNameList() {
		return nameList;
	}

	public void setNameList(List<String> nameList) {
		this.nameList = nameList;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

}
