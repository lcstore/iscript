package com.lezo.iscript.io;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import org.apache.lucene.util.NamedThreadFactory;

import com.lezo.iscript.IoGain;
import com.lezo.iscript.IoSeed;
import com.lezo.iscript.IoWatch;
import com.lezo.iscript.cache.GainCacher;
import com.lezo.iscript.cache.SeedCacher;
import com.lezo.iscript.common.BufferObjectWriter;
import com.lezo.iscript.parser.FileParser;
import com.lezo.iscript.parser.PathParser;
import com.lezo.iscript.yeam.resultmgr.writer.BufferWriterManager;

@Getter
@Log4j
public class IoDispatcher implements Runnable {
	private static final IoDispatcher INSTANCE = new IoDispatcher();
	private static final long timeout = 60000;
	private static final int PARSER_QUEUE_CAPACITY = 100;
	private boolean running = false;
	private ExecutorService executor;
	private IoClient fetchClient;
	private IoClient parseClient;

	private IoDispatcher() {
		start();
	}

	public static IoDispatcher getInstance() {
		return INSTANCE;
	}

	public synchronized void start() {
		if (isRunning()) {
			return;
		}
		executor = Executors.newFixedThreadPool(1, new NamedThreadFactory("IoDispatcher"));
		running = true;
		executor.execute(this);
		executor.shutdown();
	}

	public synchronized void stop() {
		if (!isRunning()) {
			return;
		}
		running = false;
		if (executor != null) {
			executor.shutdownNow();
		}
	}

	@Override
	public void run() {
		initIoClient();
		while (isRunning()) {
			sleep();
			addTask2Parser();
			addTask2Fetcher();
			// doFlush();
			doWatch();
			doCount();
		}
	}

	private void doCount() {
		log.info("PathFetcher:" + PathFetcher.getCount().get());
		log.info("FileFetcher:" + FileFetcher.getCount().get());
		log.info("PathParser:" + PathParser.getCount().get());
		log.info("FileParser:" + FileParser.getCount().get());
		IoClient client = getFetchClient();
		log.info("FetchClient,call:" + client.getCallCount() + ",active:" + client.getExecutor().getActiveCount()
				+ ",done:" + client.getExecutor().getCompletedTaskCount() + ",queue:"
				+ client.getExecutor().getQueue().size() + ",capacity:" + client.getCapacity());
		IoClient pClient = getParseClient();
		log.info("ParseClient,call:" + pClient.getCallCount() + ",active:" + pClient.getExecutor().getActiveCount()
				+ ",done:" + pClient.getExecutor().getCompletedTaskCount() + ",queue:"
				+ pClient.getExecutor().getQueue().size() + ",capacity:" + pClient.getCapacity());
	}

	private void doWatch() {
		for (Entry<String, IoWatch> entry : IoWatcher.getInstance().getWatchMap().entrySet()) {
			IoWatch ioWatch = entry.getValue();
			log.info("doWatch:" + entry.getKey() + ",total:" + ioWatch.getTotalCount() + ",fetch:"
					+ ioWatch.getFetchCount()
					+ ",toMills:" + ioWatch.getToMills());
		}

	}

	public void doFlush() {
		long start = System.currentTimeMillis();
		Iterator<Entry<String, BufferObjectWriter<?>>> it = BufferWriterManager.getInstance().iterator();
		int size = 0;
		log.info("start to flush writer...");
		while (it.hasNext()) {
			Entry<String, BufferObjectWriter<?>> entry = it.next();
			entry.getValue().flush();
			log.info("flush writer:" + entry.getKey());
			size++;
		}
		long cost = System.currentTimeMillis() - start;
		log.info("finish to flush writer:" + size + ",cost:" + cost);

	}

	private void addTask2Fetcher() {
		Queue<IoGain> queue = GainCacher.getInstance().getQueue();
		int size = queue.size();
		if (size > PARSER_QUEUE_CAPACITY) {
			log.warn("wait for paser.current IoGain count:" + size);
			return;
		}
		IoClient client = getFetchClient();
		int freeCount = client.getCapacity() - client.size();
		List<IoSeed> ioSeeds = SeedCacher.getInstance().getQueue().poll(freeCount);
		for (IoSeed ioSeed : ioSeeds) {
			client.execute(new IoSeedWorker(ioSeed));
		}
		log.info("add IoSeedWorker count:" + ioSeeds.size() + ",limit:" + freeCount);
	}

	private void addTask2Parser() {
		IoClient client = getParseClient();
		int freeCount = client.getCapacity() - client.size();
		Queue<IoGain> queue = GainCacher.getInstance().getQueue();
		while (!queue.isEmpty() && freeCount-- > 0) {
			IoGain ioGain = queue.poll();
			if (ioGain != null) {
				client.execute(new IoGainWorker(ioGain));
			} else {
				break;
			}
		}
		if (freeCount > 0) {
			log.info("GainCacher is empty.");
		}
	}

	private void initIoClient() {
		fetchClient = new IoClient(1, 3, 1000, 50, "fetcher");
		parseClient = new IoClient(1, 3, 1000, PARSER_QUEUE_CAPACITY, "parser");
	}

	private void sleep() {
		try {
			if (log.isDebugEnabled()) {
				log.debug("sleep milliseconds:" + timeout);
			}
			TimeUnit.MILLISECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			log.warn("", e);
		}
	}
}
