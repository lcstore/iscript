package com.lezo.iscript.yeam.resultmgr;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultHandlerWoker implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(ResultHandlerWoker.class);
	private IResultController resultController = SpringBeanUtils.getBean(IResultController.class);
	private List<ResultWritable> rWritables;
	public ResultHandlerWoker(List<ResultWritable> rWritables) {
		super();
		this.rWritables = rWritables;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		logger.info("start to handle result:" + rWritables.size());
		resultController.commit(rWritables);
		long cost = System.currentTimeMillis() - start;
		logger.info("finish to handle result:" + rWritables.size() + ",cost:" + cost);

	}

}
