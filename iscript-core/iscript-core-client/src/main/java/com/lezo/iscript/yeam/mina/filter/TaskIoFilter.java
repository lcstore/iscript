package com.lezo.iscript.yeam.mina.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.storage.ResultFutureStorager;
import com.lezo.iscript.yeam.task.TaskWorker;
import com.lezo.iscript.yeam.task.TasksCaller;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskIoFilter extends IoFilterAdapter {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(TaskIoFilter.class);

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		IoRespone ioRespone = (IoRespone) message;
		if (IoConstant.EVENT_TYPE_TASK == ioRespone.getType()) {
			addTasks(ioRespone);
		} else {
			nextFilter.messageReceived(session, message);
		}
	}

	private void addTasks(IoRespone ioRespone) {
		List<TaskWritable> taskList = getTaskList(ioRespone);
		if (CollectionUtils.isEmpty(taskList)) {
			return;
		}
		// keep ConfigResponeWorker working in the line
		ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
		ResultFutureStorager storager = ResultFutureStorager.getInstance();
		for (TaskWritable taskWritable : taskList) {
			Future<ResultWritable> future = caller.submit(new TaskWorker(taskWritable));
			storager.getStorageBuffer().add(future);
		}
		String msg = String.format("Get task:%d,Queue:%d,working:%d", taskList.size(), caller.getQueue().size(),
				caller.getActiveCount());
		logger.info(msg);
	}

	@SuppressWarnings("unchecked")
	private List<TaskWritable> getTaskList(IoRespone ioRespone) {
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
