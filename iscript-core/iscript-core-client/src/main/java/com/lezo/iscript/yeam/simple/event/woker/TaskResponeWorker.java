package com.lezo.iscript.yeam.simple.event.woker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.client.task.TaskCallable;
import com.lezo.iscript.yeam.client.task.TasksCaller;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.simple.storage.ResultFutureStorager;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskResponeWorker implements Runnable {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(TaskResponeWorker.class);
	private IoRespone ioRespone;
	private static final Object WRITE_LOCK = new Object();

	public TaskResponeWorker(IoRespone ioRespone) {
		super();
		this.ioRespone = ioRespone;
	}

	@Override
	public void run() {
		handleRespone();
	}

	private void handleRespone() {
		List<TaskWritable> taskList = getTaskList();
		if (CollectionUtils.isEmpty(taskList)) {
			return;
		}
		// keep ConfigResponeWorker working in the line
		synchronized (WRITE_LOCK) {
			ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
			ResultFutureStorager storager = ResultFutureStorager.getInstance();
			for (TaskWritable taskWritable : taskList) {
				Future<ResultWritable> future = caller.submit(new TaskCallable(taskWritable));
				storager.getStorageBuffer().add(future);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<TaskWritable> getTaskList() {
		List<TaskWritable> configList = new ArrayList<TaskWritable>();
		try {
			Object dataObject = ioRespone.getData();
			if (dataObject instanceof TaskWritable) {
				TaskWritable TaskWritable = (TaskWritable) dataObject;
				configList.add(TaskWritable);
			} else if (dataObject instanceof List) {
				configList = (List<TaskWritable>) dataObject;
			}
		} catch (Exception e) {
			logger.warn("can not cast data to config.", e);
		}
		return configList;
	}
}
