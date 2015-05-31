package com.lezo.iscript.yeam.server.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.lezo.iscript.common.buffer.StampBeanBuffer;
import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.dto.TypeConfigDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.IoAcceptorHolder;
import com.lezo.iscript.yeam.server.SendUtils;
import com.lezo.iscript.yeam.tasker.buffer.StampBufferHolder;
import com.lezo.iscript.yeam.tasker.cache.ProxyCacher;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.tasker.cache.TaskQueue;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class IoTaskHandler implements MessageHandler {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(IoTaskHandler.class);
	public static final int PER_OFFER_SIZE = 50;
	public static final int MIN_TASK_SIZE = 10;
	private static final Object LOCKER = new Object();

	@Override
	public void handleMessage(IoSession session, Object message) {
		IoRequest ioRequest = (IoRequest) message;
		if (ioRequest == null) {
			return;
		}
		ensureTaskLoaded();
		String header = ioRequest.getHeader();
		pushTasks(JSONUtils.getJSONObject(header), session);
	}

	private void ensureTaskLoaded() {
		TaskCacher taskCacher = TaskCacher.getInstance();
		while (taskCacher.getTypeCount() < 1) {
			logger.warn("wait to buffer tasks...");
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public Object pushTasks(JSONObject hObject, IoSession ioSession) {
		Integer tsize = JSONUtils.getInteger(hObject, "tsize");
		if (tsize >= MIN_TASK_SIZE) {
			return 0;
		}
		int sendCount = 0;
		List<TaskWritable> taskOffers = Collections.emptyList();
		synchronized (LOCKER) {
			taskOffers = getOfferTasks(hObject);
		}
		if (!taskOffers.isEmpty()) {
			// assignProxyForTasks(taskOffers);
			IoRespone ioRespone = new IoRespone();
			ioRespone.setType(IoConstant.EVENT_TYPE_TASK);
			ioRespone.setData(taskOffers);
			SendUtils.doSend(hObject, ioRespone, ioSession);
			sendCount = taskOffers.size();
		}
		return sendCount;
	}

	private List<TaskWritable> getOfferTasks(JSONObject hObject) {
		long start = System.currentTimeMillis();
		TaskCacher taskCancher = TaskCacher.getInstance();
		List<String> typeList = taskCancher.getNotEmptyTypeList();
		if (CollectionUtils.isEmpty(typeList)) {
			return Collections.emptyList();
		}
		List<TaskWritable> taskOffers = new ArrayList<TaskWritable>(PER_OFFER_SIZE);
		Collections.shuffle(typeList);
		logger.info(String.format("Ready type:%s", typeList));
		TaskAssign taskAssign = getTaskAssign(typeList);
		int limit = taskAssign.getMaxCountForType();
		int remain = taskAssign.getMaxCountForClient();
		int cycle = 0;
		StampBeanBuffer<TypeConfigDto> typeConfigBuffer = StampBufferHolder.getTypeConfigBuffer();
		Map<String, Integer> typCountMap = new HashMap<String, Integer>();
		while (remain > 0 && ++cycle <= 3) {
			for (String type : typeList) {
				int assignlimit = getAssignLimit(type, typCountMap, typeConfigBuffer);
				assignlimit = Math.min(limit, assignlimit);
				assignlimit = Math.min(remain, assignlimit);
				if (assignlimit < 1) {
					continue;
				}
				TaskQueue taskQueue = taskCancher.getQueue(type);
				List<TaskWritable> taskList = taskQueue.pollDecsLevel(assignlimit);
				if (!CollectionUtils.isEmpty(taskList)) {
					remain -= taskList.size();
					taskOffers.addAll(taskList);
					Integer oldCount = typCountMap.get(type);
					if (oldCount == null) {
						oldCount = 0;
					}
					typCountMap.put(type, oldCount + taskList.size());
					if (remain < 1) {
						break;
					}
				}

			}
		}
		String clientName = JSONUtils.getString(hObject, "name");
		if (!typCountMap.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("assign to client:");
			sb.append(clientName);
			sb.append(",typeCount:");
			sb.append(typeList.size());
			sb.append(",assign tasks[");
			String suffix = ", ";
			boolean hasElement = false;
			for (Entry<String, Integer> entry : typCountMap.entrySet()) {
				if (hasElement) {
					sb.append(suffix);
				}
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
				hasElement = true;
			}
			sb.append("]");
			logger.info(sb.toString());
		}
		long cost = System.currentTimeMillis() - start;
		String msg = String.format("Offer %s task for client:%s,[tactive:%s,Largest:%s,tsize:%s](%s),cost:%s",
				taskOffers.size(), clientName, JSONUtils.getString(hObject, "tactive"),
				JSONUtils.getString(hObject, "tmax"), JSONUtils.getString(hObject, "tsize"), limit, cost);
		logger.info(msg);
		return taskOffers;
	}

	private int getAssignLimit(String type, Map<String, Integer> typCountMap,
			StampBeanBuffer<TypeConfigDto> typeConfigBuffer) {
		TypeConfigDto bean = typeConfigBuffer.getBean(type);
		if (bean == null) {
			logger.warn("can not get TypeConfigDto:" + type);
			return Integer.MAX_VALUE;
		}
		if (bean.getAssignMaxSize() == null || bean.getAssignMaxSize() == -1) {
			return Integer.MAX_VALUE;
		}
		Integer hasCount = typCountMap.get(type);
		hasCount = hasCount == null ? 0 : hasCount;
		return bean.getAssignMaxSize() - hasCount;
	}

	private void assignProxyForTasks(List<TaskWritable> taskOffers) {
		if (CollectionUtils.isEmpty(taskOffers)) {
			return;
		}
		ProxyCacher proxyCacher = ProxyCacher.getInstance();
		Integer useProxy = 1;
		String defaultDomain = "baidu.com";
		Queue<ProxyDetectDto> proxyQueue = proxyCacher.getOrSecond(defaultDomain, null);
		int proxyCount = proxyQueue == null ? 0 : proxyQueue.size();
		if (proxyCount > 100) {
			logger.info("domain:" + defaultDomain + ",proxy count:" + proxyCount);
		} else {
			logger.warn("lack of proxy.domain:" + defaultDomain + ",proxy count:" + proxyCount);
		}
		for (TaskWritable task : taskOffers) {
			if (useProxy.equals(task.get("useProxy"))) {
				Object retryObject = task.get("retry");
				Integer retry = (Integer) (retryObject == null ? 0 : retryObject);
				if (retry == 3) {
					continue;
				}
				ProxyDetectDto proxyDto = proxyQueue.poll();
				if (proxyDto == null) {
					logger.warn("there is no proxy in domain:" + defaultDomain);
				} else {
					task.put("proxyPort", proxyDto.getPort());
					task.put("proxyHost", proxyDto.getIpString());
					task.put("proxyType", proxyDto.getType());
				}
				proxyQueue.offer(proxyDto);
			}
		}

	}

	private TaskAssign getTaskAssign(List<String> typeList) {
		TaskAssign assign = new TaskAssign();
		if (CollectionUtils.isEmpty(typeList)) {
			return assign;
		}
		TaskCacher taskCancher = TaskCacher.getInstance();
		int total = 0;
		for (String type : typeList) {
			total += taskCancher.getQueue(type).size();
		}
		int clientCount = IoAcceptorHolder.getIoAcceptor().getManagedSessions().size();
		int countForClient = total / clientCount;
		countForClient = countForClient < PER_OFFER_SIZE ? (countForClient < 1 ? 1 : countForClient) : PER_OFFER_SIZE;
		int countForType = countForClient / typeList.size();
		countForType = countForType < 1 ? 1 : countForType;
		assign.setMaxCountForClient(countForClient);
		assign.setMaxCountForType(countForType);
		return assign;
	}

	private static class TaskAssign {
		private int maxCountForClient;
		private int maxCountForType;

		public int getMaxCountForClient() {
			return maxCountForClient;
		}

		public void setMaxCountForClient(int maxCountForClient) {
			this.maxCountForClient = maxCountForClient;
		}

		public int getMaxCountForType() {
			return maxCountForType;
		}

		public void setMaxCountForType(int maxCountForType) {
			this.maxCountForType = maxCountForType;
		}
	}

}
