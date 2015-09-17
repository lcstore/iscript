package com.lezo.iscript.common;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CacheRejectThreadPool extends ThreadPoolExecutor {
    public CacheRejectThreadPool(int corePoolSize, int maximumPoolSize, int capacity, long keepAliveTime,
            ThreadFactory threadFactory, Queue<Runnable> cacheQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
                capacity), threadFactory, new CacheRejectedExecutionHandler(cacheQueue));
    }

    @Override
    public void execute(Runnable command) {
        super.execute(command);

    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        addRunnableFromCache();
    }

    private void addRunnableFromCache() {
        RejectedExecutionHandler handler = getRejectedExecutionHandler();
        if (handler == null) {
            return;
        }
        if (handler instanceof CacheRejectedExecutionHandler) {
            CacheRejectedExecutionHandler cacheHandler = (CacheRejectedExecutionHandler) handler;
            Queue<Runnable> cacheQueue = cacheHandler.getCacheQueue();
            while (cacheQueue.peek() != null && getQueue().offer(cacheQueue.peek())) {
                cacheQueue.poll();
            }
        }

    }
}
