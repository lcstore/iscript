package com.lezo.iscript.yeam.resultmgr;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.tasker.buffer.StrategyBuffer;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultHandlerWoker implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(ResultHandlerWoker.class);
	private List<ResultWritable> rWritables;

	public ResultHandlerWoker(List<ResultWritable> rWritables) {
		super();
		this.rWritables = rWritables;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		logger.info("start to handle result:" + rWritables.size());
		if (!CollectionUtils.isEmpty(rWritables)) {
			for (ResultWritable rWritable : rWritables) {
				doRetryHandler(rWritable, 1);
			}
		}
		long cost = System.currentTimeMillis() - start;
		logger.info("start to handle result:" + rWritables.size() + ",cost:" + cost);

	}

	private void doRetryHandler(ResultWritable rWritable, int execCount) {
		try {
			String strategyName = getStrategyName(rWritable);
			if (StringUtils.isEmpty(strategyName)) {
				return;
			}
			ResultStrategy resultStrategy = StrategyBuffer.getInstance().getStrategy(strategyName);
			if (resultStrategy == null) {
				if (execCount <= 3) {
					String msg = String.format("type:%s,strategy:%s,retry:%s", rWritable.getType(), strategyName,
							execCount);
					logger.warn(msg);
					TimeUnit.MILLISECONDS.sleep(30000);
					doRetryHandler(rWritable, execCount + 1);
				} else {
					String msg = String.format("type:%s,strategy:%s,rsObject:%s", rWritable.getType(), strategyName,
							rWritable.getResult());
					logger.warn(msg);
				}
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
