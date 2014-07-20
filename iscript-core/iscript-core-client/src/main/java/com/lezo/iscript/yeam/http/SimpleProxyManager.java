package com.lezo.iscript.yeam.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProxyManager implements ProxyManager {
	private static Logger logger = LoggerFactory.getLogger(SimpleProxyManager.class);
	public ConcurrentHashMap<String, ProxyTracker> proxyMap = new ConcurrentHashMap<String, ProxyTracker>();
	private static final Object WRITE_LOCK = new Object();
	private List<ProxyTracker> enableTrackers = new ArrayList<ProxyTracker>();
	private List<ProxyTracker> diableTrackers = new ArrayList<ProxyTracker>();
	private static final int corePoolSize = 1;
	private static final int maximumPoolSize = 2;
	private static final long keepAliveTime = 60 * 1000;
	private static final BlockingQueue<Runnable> resultQueue = new ArrayBlockingQueue<Runnable>(20);
	private static final ThreadPoolExecutor caller = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
			keepAliveTime, TimeUnit.MILLISECONDS, resultQueue);

	@Override
	public void addTracker(Long id, String host, int port) {
		String key = getKey(host, port);
		if (proxyMap.containsKey(key)) {
			return;
		}
		Proxy proxy = createProxy(host, port);
		ProxyTracker tracker = new ProxyTracker();
		tracker.setId(id);
		tracker.setProxy(proxy);
		synchronized (WRITE_LOCK) {
			proxyMap.put(key, tracker);
			enableTrackers.add(tracker);
		}
	}

	@Override
	public void trackFail(URI uri, SocketAddress sa, IOException ioe) {
		caller.execute(new TrackWorker(uri, sa, ioe));
	}

	private String getKey(String host, int port) {
		return host + ":" + port;
	}

	@Override
	public List<ProxyTracker> getEnableTrackers() {
		return enableTrackers;
	}

	public void setEnableTrackers(List<ProxyTracker> enableTrackers) {
		this.enableTrackers = enableTrackers;
	}

	@Override
	public List<ProxyTracker> getDiableTrackers() {
		return diableTrackers;
	}

	private Proxy createProxy(String host, int port) {
		SocketAddress sa = new InetSocketAddress(host, port);
		return new Proxy(Proxy.Type.HTTP, sa);
	}

	class TrackWorker implements Runnable {
		private URI uri;
		private SocketAddress sa;
		private IOException ioe;

		public TrackWorker(URI uri, SocketAddress sa, IOException ioe) {
			super();
			this.uri = uri;
			this.sa = sa;
			this.ioe = ioe;
		}

		@Override
		public void run() {
			InetSocketAddress isa = (InetSocketAddress) sa;
			String key = getKey(isa.getHostName(), isa.getPort());
			ProxyTracker tracker = proxyMap.get(key);
			tracker.trackFail(uri.toString(), ioe);
			int len = tracker.getErrorArray().length();
			if (len >= 5) {
				List<ProxyTracker> copyTrackers = new ArrayList<ProxyTracker>(enableTrackers.size());
				for (ProxyTracker eTracker : enableTrackers) {
					if (tracker.equals(eTracker)) {
						continue;
					}
					copyTrackers.add(eTracker);
				}
				synchronized (WRITE_LOCK) {
					enableTrackers = copyTrackers;
					diableTrackers.add(tracker);
					proxyMap.remove(key);
				}
			}
			logger.warn(String.format("proxy %s,error num:%d", key, len));
			logger.warn(String.format("enable proxy:%d,diable proxy:%d", enableTrackers.size(), diableTrackers.size()));
		}

	}

}
