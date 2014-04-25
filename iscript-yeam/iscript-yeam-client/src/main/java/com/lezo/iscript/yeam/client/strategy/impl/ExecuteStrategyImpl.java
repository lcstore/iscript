package com.lezo.iscript.yeam.client.strategy.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.client.event.CallEvent;
import com.lezo.iscript.yeam.client.event.EventManager;
import com.lezo.iscript.yeam.client.task.TasksCaller;
import com.lezo.iscript.yeam.client.task.TasksHolder;
import com.lezo.iscript.yeam.strategy.Strategyable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ExecuteStrategyImpl implements Strategyable {
	private static Logger log = Logger.getLogger(ExecuteStrategyImpl.class);

	@Override
	public void doStrategy() throws Exception {
		ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
		BlockingQueue<TaskWritable> taskQueue = TasksHolder.getInstance().getTaskQueue();
		int index = 0;
		EventManager eventManager = EventManager.getInstance();
		while (!taskQueue.isEmpty()) {
			try {
				TaskWritable task = taskQueue.poll();
				eventManager.notifyEvent(new CallEvent(task, CallEvent.SUBMIT_TASK_EVENT));
				index++;
			} catch (RejectedExecutionException e) {
				long waitTime = ClientConstant.EXECUTE_INTERVAL_TIME / 3;
				log.warn("wait for " + waitTime + "ms.cause:", e);
				sleep(waitTime);
			}
		}
		log.info("execute.thread[active:" + caller.getActiveCount() + ",largest:" + caller.getLargestPoolSize()
				+ ",max:" + caller.getMaximumPoolSize() + "],task[add:" + index + ",wait:" + taskQueue.size()
				+ ",work:" + caller.getQueue().size() + ",sum:" + caller.getCompletedTaskCount() + "]");
		long timeout = ClientConstant.EXECUTE_INTERVAL_TIME;
		sleep(timeout);
	}

	private void sleep(long timeout) {
		try {
			TimeUnit.MILLISECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			log.warn("sleep,cause:", e);
		}
	}

}
