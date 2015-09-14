package com.lezo.iscript.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CacheRejectThreadPool extends ThreadPoolExecutor {
    public CacheRejectThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
            ThreadFactory threadFactory,
            CacheRejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
                maximumPoolSize), threadFactory, handler);
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
            BlockingQueue<Runnable> cacheBlockingQueue = cacheHandler.getCacheBlockingQueue();
            while (cacheBlockingQueue.peek() != null && getQueue().offer(cacheBlockingQueue.peek())) {
                cacheBlockingQueue.poll();
            }
        }

    }

}
