package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.service.crawler.service.MessageService;
import com.lezo.iscript.utils.JSONUtils;
import com.qiniu.api.auth.digest.Mac;

public class DataMessageHandler {
	private static final String DIR_SEPARATOR = "/";
	private static Logger logger = LoggerFactory.getLogger(DataMessageHandler.class);
	private static AtomicBoolean running = new AtomicBoolean(false);
	@Autowired
	private Mac mac;
	@Autowired
	private MessageService messageService;
	@Autowired
	@Qualifier("dataProduceExecutor")
	private ThreadPoolExecutor executor;

	private List<String> nameList;
	private Integer limit = 50;
	private Integer maxPathCount = 100;

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
			Set<String> pathSet = new HashSet<String>();
			while (true) {
				List<MessageDto> dtoList = messageService.getMessageDtos(nameList, 0, limit);
				logger.info("Query.name:{},limit:{},query:{},size:{},last.path:{}.", nameList, limit, ++query,
						dtoList.size(), pathSet.size());
				Set<Long> idSet = new HashSet<Long>(dtoList.size());
				for (MessageDto dto : dtoList) {
					idSet.add(dto.getId());
					JSONObject mObject = JSONUtils.getJSONObject(dto.getMessage());
					if (mObject == null) {
						continue;
					}
					String dateString = getDateString(dto.getCreateTime());
					Iterator<?> it = mObject.keys();
					while (it.hasNext()) {
						StringBuilder sb = new StringBuilder("iscript/");
						sb.append(dateString);
						sb.append(DIR_SEPARATOR);
						sb.append(dto.getName());
						sb.append(DIR_SEPARATOR);
						sb.append(it.next().toString());
						pathSet.add(sb.toString());
					}
				}
				messageService.batchUpdateStatus(new ArrayList<Long>(idSet), -1, "size:" + idSet.size());
				if (pathSet.size() >= maxPathCount) {
					break;
				}
				if (dtoList.size() < limit) {
					break;
				}
			}
			buildeProducer(pathSet);
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("Finish to handle.name:%s,limit:%s,query:%s,dir:%s,cost:%s.", nameList, limit,
					query, pathSet.size(), cost);
			logger.info(msg);
		} catch (Exception e) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("Finish to handle.name:%s,limit:%s,query:%s,cost:%s.Cause:", nameList, limit,
					query, cost);
			logger.warn(msg, e);
		} finally {
			running.set(false);
		}
	}

	private void buildeProducer(Set<String> dirSet) {
		for (String dir : dirSet) {
			executor.execute(new DataLineProducer(dir));
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

	public void setMac(Mac mac) {
		this.mac = mac;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void setExecutor(ThreadPoolExecutor executor) {
		this.executor = executor;
	}

	public void setMaxPathCount(Integer maxPathCount) {
		this.maxPathCount = maxPathCount;
	}
}
