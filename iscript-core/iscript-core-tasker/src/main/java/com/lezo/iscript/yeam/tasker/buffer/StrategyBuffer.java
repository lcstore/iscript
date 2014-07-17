package com.lezo.iscript.yeam.tasker.buffer;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.loader.ClassUtils;
import com.lezo.iscript.service.crawler.dto.TaskConfigDto;
import com.lezo.iscript.yeam.strategy.ResultStrategy;

public class StrategyBuffer {
	private Logger logger = LoggerFactory.getLogger(StrategyBuffer.class);
	private ConcurrentHashMap<String, ResultStrategy> strategyMap = new ConcurrentHashMap<String, ResultStrategy>();
	private long stamp = 0;

	private StrategyBuffer() {
	}

	private static final class InstanceHolder {
		private static final StrategyBuffer INSTANCE = new StrategyBuffer();
	}

	public static StrategyBuffer getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public ResultStrategy getStrategy(String name) {
		return strategyMap.get(name);
	}

	public synchronized void addStrategy(TaskConfigDto dto) throws Exception {
		String codeSource = dto.getConfig();
		Object newObject = ClassUtils.newObject(codeSource);
		if (newObject instanceof ResultStrategy) {
			ResultStrategy resultStrategy = (ResultStrategy) newObject;
			strategyMap.put(resultStrategy.getName(), resultStrategy);
			stamp = dto.getUpdateTime().getTime();
		}
		
	}

	public long getStamp() {
		return stamp;
	}

}
