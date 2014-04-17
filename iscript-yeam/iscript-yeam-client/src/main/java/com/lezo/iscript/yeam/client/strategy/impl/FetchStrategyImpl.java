package com.lezo.iscript.yeam.client.strategy.impl;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.client.task.TasksCaller;
import com.lezo.iscript.yeam.client.task.TasksHolder;
import com.lezo.iscript.yeam.client.utils.ClientRemoteUtils;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.service.TaskerService;
import com.lezo.iscript.yeam.strategy.Strategyable;
import com.lezo.iscript.yeam.writable.ClientWritable;
import com.lezo.iscript.yeam.writable.ConfigWritable;
import com.lezo.iscript.yeam.writable.RemoteWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class FetchStrategyImpl implements Strategyable {
	private static Logger log = Logger.getLogger(FetchStrategyImpl.class);

	@Override
	public void doStrategy() throws Exception {
		long timeout = ClientConstant.FETCH_INTERVAL_TIME;
		String type = "none";
		int size = 0;
		int last = TasksHolder.getInstance().getTaskQueue().size();
		try {
			if (!isLackTasks()) {
				sleep(timeout);
			} else {
				ClientWritable client = (ClientWritable) ObjectBuilder.findObject(ClientWritable.class.getName());
				TaskerService taskerService = ClientRemoteUtils.getTaskerService();
				RemoteWritable<?> taskWrapper = taskerService.getMore(client);
				handleWritables(taskWrapper);
				type = taskWrapper.getStatus() == ClientConstant.GET_CONFIG ? "Config"
						: taskWrapper.getStatus() == ClientConstant.GET_TASK ? "Task" : "Unknow";
				size = taskWrapper.getStorageList() == null ? 0 : taskWrapper.getStorageList().size();
			}
		} catch (Exception ex) {
			log.warn("getMore cause:", ex);
			sleep(timeout);
		} finally {
			int wait = TasksHolder.getInstance().getTaskQueue().size();
			int work = TasksCaller.getInstance().getCaller().getQueue().size();
			log.info("fetch.add[" + type + ":" + size + "],task[last:" + last + ",wait:" + wait + ",work:" + work + "]");
		}

	}

	private boolean isLackTasks() {
		int wait = TasksHolder.getInstance().getTaskQueue().size();
		return wait < ClientConstant.MAX_TASK_BUFFER_SIZE;
	}

	private void handleWritables(RemoteWritable<?> taskWrapper) {
		long timeout = ClientConstant.FETCH_INTERVAL_TIME;
		switch (taskWrapper.getStatus()) {
		case ClientConstant.GET_TASK: {
			addTasks(taskWrapper);
			if (CollectionUtils.isEmpty(taskWrapper.getStorageList())) {
				log.info("Get empty tasks.sleep a moment:" + timeout + "ms");
				sleep(timeout);
			}
			break;
		}
		case ClientConstant.GET_CONFIG: {
			doConfigBuffer(taskWrapper);
			break;
		}
		case ClientConstant.GET_CLIENT: {
			log.info("Get Client,do update.sleep a moment:" + timeout + "ms");
			sleep(timeout);
			break;
		}
		default:
			break;
		}
	}

	private void sleep(long timeout) {
		try {
			TimeUnit.MILLISECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			log.warn("sleep,cause:", e);
		}
	}

	private void doConfigBuffer(RemoteWritable<?> remoteWritable) {
		if (null == remoteWritable.getStorageList()) {
			return;
		}
		ConfigParserBuffer configBuffer = ConfigParserBuffer.getInstance();
		for (Object rwObject : remoteWritable.getStorageList()) {
			if (!(rwObject instanceof ConfigWritable)) {
				continue;
			}
			ConfigWritable configWritable = (ConfigWritable) rwObject;
			configBuffer.addConfig(configWritable.getName(), configWritable);
		}
		ClientWritable client = (ClientWritable) ObjectBuilder.findObject(ClientWritable.class.getName());
		if (client.getParam() == null) {
			client.setParam(new HashMap<String, Object>());
		}
		client.getParam().put(ClientConstant.CLIENT_CONFIG_STAMP, configBuffer.getStamp());
	}

	private void addTasks(RemoteWritable<?> remoteWritable) {
		if (null == remoteWritable.getStorageList()) {
			return;
		}
		BlockingQueue<TaskWritable> taskQueue = TasksHolder.getInstance().getTaskQueue();
		for (Object rwObject : remoteWritable.getStorageList()) {
			if (!(rwObject instanceof TaskWritable)) {
				continue;
			}
			TaskWritable taskWritable = (TaskWritable) rwObject;
			taskQueue.offer(taskWritable);
		}
	}
}
