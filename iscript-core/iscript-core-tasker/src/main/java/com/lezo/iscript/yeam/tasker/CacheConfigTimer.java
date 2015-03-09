package com.lezo.iscript.yeam.tasker;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.common.buffer.StampBeanBuffer;
import com.lezo.iscript.common.buffer.StampGetable;
import com.lezo.iscript.common.loader.ClassUtils;
import com.lezo.iscript.service.crawler.dto.TaskConfigDto;
import com.lezo.iscript.service.crawler.service.TaskConfigService;
import com.lezo.iscript.service.crawler.service.TypeConfigService;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.tasker.buffer.StampBufferHolder;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class CacheConfigTimer {
	private static Logger log = Logger.getLogger(CacheConfigTimer.class);
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
		StampBeanBuffer<ConfigWritable> configBuffer = StampBufferHolder.getConfigBuffer();
		StampBeanBuffer<ResultStrategy> stragegyBuffer = StampBufferHolder.getStrategyBuffer();
		long configStamp = configBuffer.getBufferStamp();
		long strategyStamp = stragegyBuffer.getBufferStamp();
		Date afterStamp = new Date(configStamp < strategyStamp ? strategyStamp : configStamp);
		List<TaskConfigDto> configList = taskConfigService.getTaskConfigDtos(afterStamp, 1);
		if (configList.isEmpty()) {
			return;
		}
		List<TaskConfigDto> taskConfigs = new ArrayList<TaskConfigDto>(configList.size());
		List<TaskConfigDto> strategyConfigs = new ArrayList<TaskConfigDto>(configList.size());
		doAssort(configList, taskConfigs, strategyConfigs);
		add2StrategyBuffer(strategyConfigs);
		add2ConfigBuffer(taskConfigs);

	}

	private void add2StrategyBuffer(List<TaskConfigDto> strategyConfigs) {
		if (strategyConfigs.isEmpty()) {
			return;
		}
		StampBeanBuffer<ResultStrategy> stragegyBuffer = StampBufferHolder.getStrategyBuffer();
		StringBuilder sb = new StringBuilder();
		for (final TaskConfigDto dto : strategyConfigs) {
			try {
				ResultStrategy resultStrategy = convertTo(dto);
				ResultStrategy oldStrategy = stragegyBuffer.addBean(resultStrategy, new StampGetable<ResultStrategy>() {
					@Override
					public String getName(ResultStrategy bean) {
						return bean.getName();
					}

					@Override
					public long getStamp(ResultStrategy bean) {
						return dto.getUpdateTime().getTime();
					}
				});
				handleOldStrategy(oldStrategy);
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(dto.getType());
			} catch (Exception e) {
				log.warn("can not buffer config:" + dto.getType() + "," + ExceptionUtils.getStackTrace(e));
			}
		}
		log.info("update strategy[" + sb.toString() + "].size:" + strategyConfigs.size() + ",stamp:" + stragegyBuffer.getBufferStamp());
	}

	private void handleOldStrategy(ResultStrategy oldStrategy) {
		if (oldStrategy == null) {
			return;
		}
		if (oldStrategy instanceof Closeable) {
			Closeable closeable = (Closeable) oldStrategy;
			IOUtils.closeQuietly(closeable);
		}
		oldStrategy = null;
	}

	private ResultStrategy convertTo(TaskConfigDto dto) throws Exception {
		String codeSource = dto.getConfig();
		Object newObject = ClassUtils.newObject(codeSource);
		return (ResultStrategy) newObject;
	}

	private void add2ConfigBuffer(List<TaskConfigDto> taskConfigs) {
		if (taskConfigs.isEmpty()) {
			return;
		}
		List<ConfigWritable> configWritableList = new ArrayList<ConfigWritable>(taskConfigs.size());
		StringBuilder sb = new StringBuilder();
		for (TaskConfigDto dto : taskConfigs) {
			ConfigWritable configWritable = getConfigWritable(dto);
			configWritableList.add(configWritable);
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(dto.getType());
		}
		StampBeanBuffer<ConfigWritable> configBuffer = StampBufferHolder.getConfigBuffer();
		configBuffer.addAll(configWritableList, new StampGetable<ConfigWritable>() {
			@Override
			public long getStamp(ConfigWritable bean) {
				return bean.getStamp();
			}

			@Override
			public String getName(ConfigWritable bean) {
				return bean.getName();
			}
		});
		log.info("update config[" + sb.toString() + "].size:" + taskConfigs.size() + ",newStamp:" + configBuffer.getBufferStamp());
	}

	private void doAssort(List<TaskConfigDto> configList, List<TaskConfigDto> taskConfigs,
			List<TaskConfigDto> strategyConfigs) {
		for (TaskConfigDto dto : configList) {
			if (TaskConfigDto.DEST_CONFIG == dto.getDestType()) {
				taskConfigs.add(dto);
			} else if (TaskConfigDto.DEST_STRATEGY == dto.getDestType()) {
				strategyConfigs.add(dto);
			} else {
				log.warn("unkonw config destType, type:" + dto.getType() + ",source:" + dto.getSource());
			}
		}
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
