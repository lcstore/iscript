package com.lezo.iscript.yeam.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.crawler.utils.HttpClientUtils;

public class HttpDriver {
	private int coreSize = 3;
	private int maxSize = 5;
	private DefaultHttpClient client;
	private BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(1000);
	private ExecutorService executor = new ThreadPoolExecutor(coreSize, maxSize, 60000L, TimeUnit.MILLISECONDS,
			taskQueue);

	public HttpDriver() {
		client = HttpClientUtils.createHttpClient();
		SchemeRegistry schreg = client.getConnectionManager().getSchemeRegistry();
		HttpRoutePlanner routePlanner = new ProxySelectorRoutePlanner(schreg, new SimpleProxySelector(getProxyList()));
		client.setRoutePlanner(routePlanner);
	}

	class SimpleProxySelector extends ProxySelector {
		private Logger logger = LoggerFactory.getLogger(SimpleProxySelector.class);
		private List<Proxy> proxyList;
		private ConcurrentHashMap<String, Integer> failProxySumMap = new ConcurrentHashMap<String, Integer>();

		public SimpleProxySelector(List<Proxy> proxyList) {
			super();
			this.proxyList = proxyList;
		}

		@Override
		public List<Proxy> select(URI uri) {
			Random rand = new Random();
			int index = rand.nextInt(proxyList.size());
			List<Proxy> select = new ArrayList<Proxy>();
			Proxy proxy = proxyList.get(index);
			Integer failCount = failProxySumMap.get(proxy.address().toString());
			if (failCount != null && failCount >= 3) {
				logger.warn("give up to use proxy:" + proxy + " fail:" + failCount);
			} else {
				select.add(proxy);
			}
			return select;
		}

		@Override
		public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
			String key = sa.toString();
			Integer failCount = failProxySumMap.get(key);
			if (failCount == null) {
				failProxySumMap.put(key, 1);
			} else {
				failProxySumMap.put(key, failCount + 1);
			}

		}
	}

	public List<Proxy> getProxyList() {
		List<Proxy> proxyList = new ArrayList<Proxy>();
		proxyList.add(createProxy("5.135.176.41", 3123));
		proxyList.add(createProxy("188.241.141.112", 3127));
		proxyList.add(createProxy("198.52.199.152", 8089));
		proxyList.add(createProxy("23.89.198.161", 7808));
		proxyList.add(createProxy("198.52.217.44", 3127));
		proxyList.add(createProxy("93.115.8.229", 7808));
		return proxyList;
	}

	private Proxy createProxy(String host, int port) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SocketAddress sa = new InetSocketAddress(addr, port);
		return new Proxy(Proxy.Type.HTTP, sa);
	}

	public DefaultHttpClient getClient() {
		return client;
	}

	public void setClient(DefaultHttpClient client) {
		this.client = client;
	}

	public void execute(Runnable command) {
		executor.execute(command);
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public BlockingQueue<Runnable> getTaskQueue() {
		return taskQueue;
	}

}
