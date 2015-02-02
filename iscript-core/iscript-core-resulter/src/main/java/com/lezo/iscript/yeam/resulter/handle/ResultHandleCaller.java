package com.lezo.iscript.yeam.resulter.handle;

import java.util.List;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultHandleCaller implements Runnable {
	private List<ResultWritable> resultList;

	public ResultHandleCaller(List<ResultWritable> resultList) {
		super();
		this.resultList = resultList;
	}

	@Override
	public void run() {
		LoggerResultHandler loggerResultHandler = new LoggerResultHandler();
		QiniuResultHandler qiniuResultHandler = SpringBeanUtils.getBean(QiniuResultHandler.class);
		for (ResultWritable rw : resultList) {
			loggerResultHandler.handle(rw);
			qiniuResultHandler.handle(rw);
		}
	}

}
