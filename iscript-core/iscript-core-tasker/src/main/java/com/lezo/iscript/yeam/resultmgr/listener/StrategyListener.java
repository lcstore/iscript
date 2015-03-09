package com.lezo.iscript.yeam.resultmgr.listener;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.buffer.StampBeanBuffer;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.strategy.DefaultStrategy;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.tasker.buffer.StampBufferHolder;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class StrategyListener implements IResultListener {
	private static Logger logger = LoggerFactory.getLogger(StrategyListener.class);
	private ResultStrategy defaultStrategy = new DefaultStrategy();
	private StampBeanBuffer<ResultStrategy> stragegyBuffer = StampBufferHolder.getStrategyBuffer();

	@Override
	public void handle(ResultWritable result) {
		if (ResultWritable.RESULT_SUCCESS != result.getStatus()) {
			return;
		}
		doStrategy(result, 1);
	}

	public ResultStrategy getResultStrategy(String name) {
		if (name == null) {
			return defaultStrategy;
		}

		return stragegyBuffer.getBean(name);
	}

	private void doStrategy(ResultWritable rWritable, int execCount) {
		String strategyName = getStrategyName(rWritable);
		if (StringUtils.isEmpty(strategyName)) {
			return;
		}
		try {
			ResultStrategy resultStrategy = getResultStrategy(strategyName);
			if (resultStrategy == null) {
				String msg = String.format("Can not found strategy:%s,type:%s", strategyName, rWritable.getType());
				logger.warn(msg);
			} else {
				resultStrategy.handleResult(rWritable);
			}
		} catch (Exception e) {
			String msg = String.format("tid:%s,type:%s,cause:%s", rWritable.getTaskId(), rWritable.getType(),
					ExceptionUtils.getStackTrace(e));
			logger.warn(msg);
		}
	}

	private String getStrategyName(ResultWritable rw) {
		JSONObject rsObject = JSONUtils.getJSONObject(rw.getResult());
		if (rsObject == null) {
			return null;
		}
		JSONObject argsObject = JSONUtils.get(rsObject, "args");
		if (argsObject == null) {
			return null;
		}
		return JSONUtils.getString(argsObject, "strategy");
	}

}
