package com.lezo.iscript.yeam.tasker;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.TaskConfigDto;
import com.lezo.iscript.service.crawler.service.TaskConfigService;
import com.lezo.iscript.service.crawler.service.TypeConfigService;
import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class AssembleTaskTimer {
	private static Logger log = Logger.getLogger(AssembleTaskTimer.class);
	private static volatile boolean running = false;
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
			// doAssemble();
		} finally {
			running = false;
		}
	}

	private void updateConfig() {
		ConfigBuffer configBuffer = ConfigBuffer.getInstance();
		Date afterStamp = new Date(configBuffer.getStamp());
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
