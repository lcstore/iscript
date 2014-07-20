package com.lezo.iscript.yeam.http;

import java.net.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;

public class ProxyTracker {
	private Long id;
	private Proxy proxy;
	private AtomicInteger totalNum = new AtomicInteger(0);
	private JSONArray errorArray = new JSONArray();

	public Proxy trackCall() {
		totalNum.incrementAndGet();
		return proxy;
	}

	public void trackFail(String url, Exception ex) {
		JSONObject failObject = new JSONObject();
		JSONUtils.put(failObject, "url", url);
		JSONUtils.put(failObject, "stamp", System.currentTimeMillis());
		JSONUtils.put(failObject, "ex", ExceptionUtils.getStackTrace(ex));
		errorArray.put(failObject);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public AtomicInteger getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(AtomicInteger totalNum) {
		this.totalNum = totalNum;
	}

	public JSONArray getErrorArray() {
		return errorArray;
	}

	public void setErrorArray(JSONArray errorArray) {
		this.errorArray = errorArray;
	}

	@Override
	public int hashCode() {
		return proxy.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProxyTracker other = (ProxyTracker) obj;
		return new EqualsBuilder().append(proxy, other.getProxy()).isEquals();
	}
}
