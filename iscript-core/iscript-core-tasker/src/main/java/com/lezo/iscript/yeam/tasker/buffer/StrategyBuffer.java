package com.lezo.iscript.yeam.tasker.buffer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
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
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		ensureBuffered();
		return strategyMap.get(name);
	}

	private void ensureBuffered() {
		if (stamp > 0) {
			return;
		}
		synchronized (this) {
			if (stamp == 0) {
				try {
					long timeout = 60000L;
					logger.info("Wait to buffer strategy {}ms", timeout);
					TimeUnit.MILLISECONDS.sleep(timeout);
				} catch (InterruptedException e) {
					logger.info("", e);
				}
			}

		}

	}

	public synchronized void addStrategy(TaskConfigDto dto) throws Exception {
		String codeSource = dto.getConfig();
		Object newObject = ClassUtils.newObject(codeSource);
		if (newObject instanceof ResultStrategy) {
			ResultStrategy resultStrategy = (ResultStrategy) newObject;
			strategyMap.put(resultStrategy.getName(), resultStrategy);
		}
		long cStamp = dto.getUpdateTime().getTime();
		stamp = cStamp > stamp ? cStamp : stamp;

	}

	public long getStamp() {
		return stamp;
	}

}
