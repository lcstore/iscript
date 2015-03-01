package com.lezo.iscript.yeam.tasker.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskContainer {
	private static Logger log = LoggerFactory.getLogger(TaskContainer.class);
	private static final int ONE_PACK_SIZE = 200;
	private static final int capacity = 10000;
	private static final long CONSUME_TIMEOUT = 60000;
	private List<TaskWritable> taskList = new ArrayList<TaskWritable>(ONE_PACK_SIZE);
	private Queue<List<TaskWritable>> consumeQueue = new ArrayBlockingQueue<List<TaskWritable>>(capacity);
	private AtomicLong lastStamp = new AtomicLong(System.currentTimeMillis());
	private static TaskContainer instance = new TaskContainer();

	private TaskContainer() {
	}

	public static TaskContainer getInstance() {
		return instance;
	}

	public void add(List<TaskWritable> taskList) {
		if (CollectionUtils.isEmpty(taskList)) {
			return;
		}
		synchronized (taskList) {
			for (TaskWritable taskDto : taskList) {
				taskList.add(taskDto);
				if (isTime2Consume()) {
					List<TaskWritable> copyList = toConsume();
					consumeQueue.offer(copyList);
				}
			}
		}
		lastStamp.set(System.currentTimeMillis());
	}

	public List<TaskWritable> poll() {
		List<TaskWritable> consumeDtos = consumeQueue.poll();
		if (consumeDtos == null && isTime2Consume()) {
			consumeDtos = toConsume();
		}
		return consumeDtos == null ? new ArrayList<TaskWritable>() : consumeDtos;
	}

	private boolean isTime2Consume() {
		if (taskList.isEmpty()) {
			return false;
		}
		if (taskList.size() >= ONE_PACK_SIZE) {
			return true;
		}
		boolean isTimeOut = System.currentTimeMillis() - lastStamp.get() > CONSUME_TIMEOUT;
		return isTimeOut;
	}

	private List<TaskWritable> toConsume() {
		List<TaskWritable> copyList = Collections.emptyList();
		if (taskList.isEmpty()) {
			return copyList;
		}
		synchronized (taskList) {
			if (!taskList.isEmpty()) {
				copyList = Arrays.asList(new TaskWritable[taskList.size()]);
				Collections.copy(copyList, taskList);
				taskList.clear();
			}
		}
		log.info("move tasks to consume,size:" + copyList.size());
		return copyList;
	}
}
