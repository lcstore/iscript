package com.lezo.iscript.yeam.tasker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;
import com.lezo.iscript.yeam.tasker.buffer.TaskBuffer;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.tasker.dto.TaskConfigDto;
import com.lezo.iscript.yeam.tasker.dto.TypeConfigDto;
import com.lezo.iscript.yeam.tasker.service.TaskConfigService;
import com.lezo.iscript.yeam.tasker.service.TypeConfigService;
import com.lezo.iscript.yeam.writable.ConfigWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class AssembleTaskTimer {
	private static Logger log = Logger.getLogger(AssembleTaskTimer.class);
	private static volatile boolean running = false;
	private int maxPackSize = 100;
	private int minPackSize = 20;
	private String tasker;
	@Autowired
	private TypeConfigService typeConfigService;
	@Autowired
	private TaskConfigService taskConfigService;

	public void run() {
		if (running) {
			log.warn("Task assemble is working...");
			return;
		}
		try {
			running = true;
			updateConfig();
			doAssemble();
		} finally {
			running = false;
		}
	}

	private void doAssemble() {
		TaskBuffer taskBuffer = TaskBuffer.getInstance();
		List<TypeConfigDto> typeConfigList = typeConfigService.getEnableTypeConfigDtos(tasker);
		int hasSize = taskBuffer.getPackQueue().size();
		if (hasSize > minPackSize) {
			return;
		}
		int addSize = maxPackSize - hasSize;
		int remain = addSize;
		List<TaskWritable> taskWritables = new ArrayList<TaskWritable>();
		while (remain > 0) {
			int lastRemain = remain;
			int lastSize = taskWritables.size();
			taskWritables = packList(typeConfigList, taskWritables, remain);
			// if no tasks,finish packing
			if (lastRemain == remain && lastSize == taskWritables.size()) {
				break;
			}
		}
		if (packOne(taskWritables, taskBuffer)) {
			remain--;
		}
		String cacheMsg = remain > 0 ? ".cache empty." : "";
		log.info("tasker[" + tasker + "],pack task.add:" + (addSize - remain) + ",queue:"
				+ taskBuffer.getPackQueue().size() + cacheMsg);
	}

	private List<TaskWritable> packList(List<TypeConfigDto> typeConfigList, List<TaskWritable> taskWritables, int remain) {
		int onePackSize = 10;
		TaskCacher taskCacher = TaskCacher.getInstance();
		TaskBuffer taskBuffer = TaskBuffer.getInstance();
		for (TypeConfigDto configDto : typeConfigList) {
			Queue<TaskWritable> queue = taskCacher.getQueue(configDto.getType());
			TaskWritable task = queue.poll();
			if (task == null) {
				continue;
			}
			taskWritables.add(task);
			if (taskWritables.size() >= onePackSize) {
				if (packOne(taskWritables, taskBuffer)) {
					taskWritables = new ArrayList<TaskWritable>(onePackSize);
					remain--;
				}
			}
		}
		return taskWritables;
	}

	private boolean packOne(List<TaskWritable> taskWritables, TaskBuffer taskBuffer) {
		if (CollectionUtils.isEmpty(taskWritables)) {
			return false;
		}
		taskBuffer.getPackQueue().offer(taskWritables);
		return true;
	}

	private void updateConfig() {
		ConfigBuffer configBuffer = ConfigBuffer.getInstance();
		Date afterStamp = null;
		if (configBuffer.getStamp() > 0) {
			afterStamp = new Date(configBuffer.getStamp());
		}
		List<TaskConfigDto> configList = taskConfigService.getTaskConfigDtos(afterStamp, 1);
		if (configList.isEmpty()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (TaskConfigDto dto : configList) {
			ConfigWritable configWritable = getConfigWritable(dto);
			configBuffer.addConfig(configWritable.getName(), configWritable);
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(dto.getType());
		}
		log.info("update config[" + sb.toString() + "].size:" + configList.size());
	}

	private ConfigWritable getConfigWritable(TaskConfigDto dto) {
		int mode = getConfityMode(dto);
		ConfigWritable configWritable = new ConfigWritable();
		configWritable.setName(dto.getType());
		configWritable.setType(mode);
		configWritable.setStamp(dto.getUpdateTime().getTime());
		String sContent = dto.getConfig();
		if (StringUtils.isEmpty(sContent)) {
			sContent = "";
		}
		configWritable.setContent(sContent.getBytes());
		return configWritable;
	}

	private int getConfityMode(TaskConfigDto dto) {
		int mode = ConfigWritable.CONFIG_TYPE_SCRIPT;
		String name = dto.getSource();
		if (name == null) {
			return mode;
		}
		String lowCaseName = name.toLowerCase();
		if (lowCaseName.endsWith(".xml")) {
			mode = ConfigWritable.CONFIG_TYPE_SCRIPT;
		} else if (lowCaseName.endsWith(".java")) {
			mode = ConfigWritable.CONFIG_TYPE_JAVA;
		}
		return mode;
	}

	public void setTasker(String tasker) {
		this.tasker = tasker;
	}
}
