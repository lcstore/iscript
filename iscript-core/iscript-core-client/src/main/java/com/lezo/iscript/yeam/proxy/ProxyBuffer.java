package com.lezo.iscript.yeam.proxy;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;

public class ProxyBuffer {
	private static Logger logger = Logger.getLogger(ConfigParserBuffer.class);
	private Object WRITE_LOCK = new Object();
	private ConcurrentHashMap<Proxy, JSONObject> useMap = new ConcurrentHashMap<Proxy, JSONObject>();
	private ConcurrentHashMap<Proxy, JSONObject> errorMap = new ConcurrentHashMap<Proxy, JSONObject>();
	private volatile long stamp = 0;

	private ProxyBuffer() {
	}

	private static final class InstanceHolder {
		private static final ProxyBuffer INSTANCE = new ProxyBuffer();
	}

	public static ProxyBuffer getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void addProxy(Long id, Proxy proxy) {
		if (!(proxy.address() instanceof InetSocketAddress)) {
			logger.warn("unhandle SocketAddress:" + proxy.address().getClass().getName());
			return;
		}
		InetSocketAddress addr = (InetSocketAddress) proxy.address();
		JSONObject proxyObject = new JSONObject();
		JSONUtils.put(proxyObject, "id", id);
		JSONUtils.put(proxyObject, "host", addr.getHostName());
		JSONUtils.put(proxyObject, "port", addr.getPort());
		JSONUtils.put(proxyObject, "total", 0);
		JSONUtils.put(proxyObject, "errors", new JSONArray());
		synchronized (useMap) {
			useMap.putIfAbsent(proxy, proxyObject);
			stamp = System.currentTimeMillis();
		}
		logger.info(String.format("enable proxy:%d,diable proxy:%d", useMap.size(), errorMap.size()));
	}

	public void addCall(Proxy proxy, String url) {
		JSONObject proxyObject = useMap.get(proxy);
		if (proxyObject == null) {
			return;
		}
		synchronized (proxyObject) {
			int total = JSONUtils.getInteger(proxyObject, "total") + 1;
			JSONUtils.put(proxyObject, "total", total);
		}
	}

	public void addError(Proxy proxy, String url, Exception ex) {
		if (ex == null) {
			return;
		}
		JSONObject proxyObject = useMap.get(proxy);
		if (proxyObject == null) {
			return;
		}
		synchronized (WRITE_LOCK) {
			JSONArray errorArray = JSONUtils.get(proxyObject, "errors");
			JSONObject errorObject = new JSONObject();
			JSONUtils.put(errorObject, "url", url);
			JSONUtils.put(errorObject, "ex", ExceptionUtils.getStackTrace(ex));
			errorArray.put(errorObject);
			if (errorArray.length() >= 5) {
				useMap.remove(proxy);
				errorMap.put(proxy, proxyObject);
			}
			int total = JSONUtils.getInteger(proxyObject, "total");
			logger.warn(String.format("proxy %s,total:%d,error:%d", proxy, total, errorArray.length()));
			logger.info(String.format("enable proxy:%d,diable proxy:%d", useMap.size(), errorMap.size()));
		}
	}

	public Proxy createProxy(String host, int port) {
		SocketAddress sa = new InetSocketAddress(host, port);
		return new Proxy(Proxy.Type.HTTP, sa);
	}

	public long getStamp() {
		return stamp;
	}

	public List<Proxy> getProxys() {
		return new ArrayList<Proxy>(useMap.keySet());
	}

	public List<JSONObject> getErrors() {
		return new ArrayList<JSONObject>(errorMap.values());
	}

	public void clearErrors() {
		errorMap.clear();
	}

	public synchronized void setStamp(long stamp) {
		this.stamp = stamp;
	}
}
