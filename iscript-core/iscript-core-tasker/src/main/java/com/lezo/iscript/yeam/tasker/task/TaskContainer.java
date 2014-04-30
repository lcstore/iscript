package com.lezo.iscript.yeam.tasker.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;

public class TaskContainer {
	private static Logger log = LoggerFactory.getLogger(TaskContainer.class);
	private static final int ONE_PACK_SIZE = 200;
	private static final int capacity = 10000;
	private static final long CONSUME_TIMEOUT = 60000;
	private List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(ONE_PACK_SIZE);
	private Queue<List<TaskPriorityDto>> consumeQueue = new ArrayBlockingQueue<List<TaskPriorityDto>>(capacity);
	private AtomicLong lastStamp = new AtomicLong(System.currentTimeMillis());
	private static TaskContainer instance = new TaskContainer();

	private TaskContainer() {
	}

	public static TaskContainer getInstance() {
		return instance;
	}

	public void add(TaskPriorityDto taskDto) {
		if (taskDto == null) {
			return;
		}
		synchronized (taskList) {
			taskList.add(taskDto);
			if (isTime2Consume()) {
				List<TaskPriorityDto> copyList = toConsume();
				consumeQueue.offer(copyList);
			}
		}
		lastStamp.set(System.currentTimeMillis());
	}

	public List<TaskPriorityDto> poll() {
		List<TaskPriorityDto> consumeDtos = consumeQueue.poll();
		if (consumeDtos == null && isTime2Consume()) {
			consumeDtos = toConsume();
		}
		return consumeDtos == null ? new ArrayList<TaskPriorityDto>() : consumeDtos;
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

	private List<TaskPriorityDto> toConsume() {
		List<TaskPriorityDto> copyList = Collections.emptyList();
		if (taskList.isEmpty()) {
			return copyList;
		}
		synchronized (taskList) {
			if (!taskList.isEmpty()) {
				copyList = Arrays.asList(new TaskPriorityDto[taskList.size()]);
				Collections.copy(copyList, taskList);
				taskList.clear();
			}
		}
		log.info("move tasks to consume,size:" + copyList.size());
		return copyList;
	}
}
