package com.lezo.iscript.yeam.result;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
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
				try {
					ResultStrategy resultStrategy = getResultStrategy(rWritable);
					if (resultStrategy == null) {
						String msg = String.format("type:%s,rsObject:%s", rWritable.getType(), rWritable.getResult());
						logger.info(msg);
					} else {
						resultStrategy.handleResult(rWritable);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		long cost = System.currentTimeMillis() - start;
		logger.info("start to handle result:" + rWritables.size() + ",cost:" + cost);

	}

	private ResultStrategy getResultStrategy(ResultWritable rw) {
		ResultStrategy resultStrategy = null;
		JSONObject rsObject = JSONUtils.getJSONObject(rw.getResult());
		if (rsObject == null) {
			return resultStrategy;
		}
		JSONObject argsObject = JSONUtils.get(rsObject, "args");
		if (argsObject == null) {
			return resultStrategy;
		}
		String strategyName = JSONUtils.getString(argsObject, "strategy");
		if (strategyName == null) {
			return resultStrategy;
		}
		resultStrategy = StrategyBuffer.getInstance().getStrategy(strategyName);
		if(resultStrategy == null){
			logger.warn("can not found strategy:"+strategyName);
		}
		return resultStrategy;
	}
}
