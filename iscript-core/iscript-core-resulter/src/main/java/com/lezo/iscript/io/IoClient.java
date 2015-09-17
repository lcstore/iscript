package com.lezo.iscript.io;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;

import org.apache.lucene.util.NamedThreadFactory;

import com.lezo.iscript.common.CacheRejectThreadPool;
import com.lezo.iscript.common.queue.RankGetter;
import com.lezo.iscript.common.queue.RankQueue;

@Getter
public class IoClient {
    private ThreadPoolExecutor executor;
    private AtomicLong fromMills = new AtomicLong();
    private AtomicLong toMills = new AtomicLong();
    private AtomicLong callCount = new AtomicLong();
    private final int capacity;

    public IoClient(int corePoolSize, int maximumPoolSize, long keepAliveTime,
            String name, int capacity, int cache) {
        super();
        this.capacity = capacity;
        RankGetter rankGetter = new RankGetter() {
            @Override
            public int getRank(Object source) {
                if (source instanceof IoSeedWorker) {
                    IoSeedWorker ioSeedWorker = (IoSeedWorker) source;
                    return ioSeedWorker.getIoSeed().getLevel();
                }
                if (source instanceof IoGainWorker) {
                    IoGainWorker ioGainWorker = (IoGainWorker) source;
                    return ioGainWorker.getIoGain().getIoSeed().getLevel();
                }
                return -1;
            }
        };
        executor = new CacheRejectThreadPool(corePoolSize, maximumPoolSize, this.capacity, keepAliveTime,
                new NamedThreadFactory(name), new RankQueue<Runnable>(rankGetter, cache));

    }

    public void execute(Runnable command) {
        executor.execute(command);
        callCount.incrementAndGet();
        fromMills.compareAndSet(0, System.currentTimeMillis());
        toMills.set(System.currentTimeMillis());
    }

    public int size() {
        return this.executor.getQueue().size();
    }

}
