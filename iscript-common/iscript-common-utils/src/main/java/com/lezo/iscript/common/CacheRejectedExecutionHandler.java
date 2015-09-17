package com.lezo.iscript.common;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class CacheRejectedExecutionHandler implements RejectedExecutionHandler {
    private Queue<Runnable> cacheQueue;

    public CacheRejectedExecutionHandler() {
        this(new LinkedBlockingQueue<Runnable>());
    }

    public CacheRejectedExecutionHandler(Queue<Runnable> cacheQueue) {
        super();
        this.cacheQueue = cacheQueue;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        getCacheQueue().offer(r);

    }

    public Queue<Runnable> getCacheQueue() {
        return cacheQueue;
    }

}
