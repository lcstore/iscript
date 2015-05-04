package com.lezo.iscript.yeam.resultmgr;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.lucene.util.NamedThreadFactory;

import com.lezo.iscript.common.ArrayBlockingQueueThreadPool;
import com.lezo.iscript.common.BlockPolicy;

public class ExecutorUtils {

	private static final ThreadPoolExecutor fileProduceExecutor = new ArrayBlockingQueueThreadPool(1, 2, 60000, 100,
			new NamedThreadFactory("fileProduce"), new BlockPolicy());
	private static final ThreadPoolExecutor fileConsumeExecutor = new ArrayBlockingQueueThreadPool(2, 3, 60000, 500,
			new NamedThreadFactory("fileConsume"), new BlockPolicy());
	private static final ThreadPoolExecutor dataConsumeExecutor = new ArrayBlockingQueueThreadPool(2, 5, 60000, 1000,
			new NamedThreadFactory("dataConsume"), new BlockPolicy());

	public static ThreadPoolExecutor getFileProduceExecutor() {
		return fileProduceExecutor;
	}

	public static ThreadPoolExecutor getFileConsumeExecutor() {
		return fileConsumeExecutor;
	}

	public static ThreadPoolExecutor getDataConsumeExecutor() {
		return dataConsumeExecutor;
	}
}
