package com.lezo.iscript.yeam.tasker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.common.queue.QueueContianer;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class FlushTaskTimer {
	private static final int ONE_BATCH_SIZE = 200;
	private static Logger log = Logger.getLogger(FlushTaskTimer.class);
	private static volatile boolean running = false;
	@Autowired
	private TaskPriorityService taskPriorityService;

	public void run() {
		if (running) {
			log.warn("FlushTaskTimer is working...");
			return;
		}
		try {
			running = true;
			Queue<TaskWritable> taskQueue = QueueContianer.getInstance().getQueue(TaskWritable.class);
			if (taskQueue == null) {
				log.warn("Do not have queue[TaskWritable]..");
				return;
			}
			int total = 0;
			long startFlush = System.currentTimeMillis();
			while (true) {
				long start = System.currentTimeMillis();
				List<TaskPriorityDto> taskDtos = getTaskList(taskQueue);
				int size = taskDtos.size();
				if (size < 1) {
					break;
				}
				taskPriorityService.batchInsert(taskDtos);
				total += size;
				long oneCost = System.currentTimeMillis() - start;
				log.info("<task.insert>.size:" + size + ",total:" + total + ",cost:" + oneCost + "ms");
			}
			long cost = System.currentTimeMillis() - startFlush;
			log.info("<task.insert>.total:" + total + ",cost:" + cost + "ms");
		} finally {
			running = false;
		}

	}

	private List<TaskPriorityDto> getTaskList(Queue<TaskWritable> taskQueue) {
		List<TaskPriorityDto> taskDtos = new ArrayList<TaskPriorityDto>(ONE_BATCH_SIZE);
		int limit = ONE_BATCH_SIZE;
		while (limit-- > 0) {
			TaskWritable taskWritable = taskQueue.poll();
			if (taskWritable == null) {
				break;
			}
			TaskPriorityDto taskPriorityDto = toTaskDto(taskWritable);
			taskDtos.add(taskPriorityDto);
		}
		return taskDtos;
	}

	private TaskPriorityDto toTaskDto(TaskWritable taskWritable) {
		JSONObject argsObject = JSONUtils.getJSONObject(taskWritable.getArgs());
		TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
		taskPriorityDto.setBatchId(JSONUtils.getString(argsObject, "bid"));
		taskPriorityDto.setType(JSONUtils.getString(argsObject, "type"));
		taskPriorityDto.setUrl(JSONUtils.getString(argsObject, "url"));
		taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
		taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
		Date createTime = JSONUtils.get(argsObject, "ctime");
		createTime = createTime == null ? new Date() : createTime;
		taskPriorityDto.setCreatTime(createTime);
		taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
		argsObject.remove("bid");
		argsObject.remove("type");
		argsObject.remove("url");
		argsObject.remove("level");
		argsObject.remove("src");
		argsObject.remove("ctime");
		taskPriorityDto.setParams(argsObject.toString());
		return taskPriorityDto;
	}
}
