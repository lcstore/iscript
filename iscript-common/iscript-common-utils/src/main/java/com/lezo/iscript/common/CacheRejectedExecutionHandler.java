package com.lezo.iscript.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class CacheRejectedExecutionHandler implements RejectedExecutionHandler {
    private BlockingQueue<Runnable> cacheBlockingQueue;

    public CacheRejectedExecutionHandler() {
        this(new LinkedBlockingQueue<Runnable>());
    }

    public CacheRejectedExecutionHandler(BlockingQueue<Runnable> cacheBlockingQueue) {
        super();
        this.cacheBlockingQueue = cacheBlockingQueue;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        getCacheBlockingQueue().offer(r);

    }

    public BlockingQueue<Runnable> getCacheBlockingQueue() {
        return cacheBlockingQueue;
    }

}
