package com.lezo.iscript.yeam.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections4.CollectionUtils;

public class SimpleProxySelector extends ProxySelector {
	private ProxyManager proxyManager;

	public SimpleProxySelector() {
		this(new SimpleProxyManager());
	}

	public SimpleProxySelector(ProxyManager proxyManager) {
		super();
		this.proxyManager = proxyManager;
	}

	@Override
	public List<Proxy> select(URI uri) {
		List<ProxyTracker> proxyTrackers = proxyManager.getEnableTrackers();
		List<Proxy> select = new ArrayList<Proxy>(1);
		if (CollectionUtils.isEmpty(proxyTrackers)) {
			select.add(Proxy.NO_PROXY);
		} else {
			Random rand = new Random();
			int index = rand.nextInt(proxyTrackers.size());
			ProxyTracker proxy = proxyTrackers.get(index);
			select.add(proxy.trackCall());
		}
		return select;
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		proxyManager.trackFail(uri, sa, ioe);
	}

	public ProxyManager getProxyManager() {
		return proxyManager;
	}

	public void setProxyManager(ProxyManager proxyManager) {
		this.proxyManager = proxyManager;
	}

}
