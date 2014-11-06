package com.lezo.iscript.service.crawler.intercept;

public class BatchUpdateMethod extends BatchMethod {
	private static final String BATCH_UPDATE_DEFAULT_NAME = "batchUpdate";
	private static final int BATCH_UPDATE_DEFAULT_INDEX = 0;

	public BatchUpdateMethod() {
		setName(BATCH_UPDATE_DEFAULT_NAME);
		setIndex(BATCH_UPDATE_DEFAULT_INDEX);
	}
}
