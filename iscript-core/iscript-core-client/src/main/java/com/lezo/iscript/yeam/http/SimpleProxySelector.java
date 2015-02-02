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

import com.lezo.iscript.yeam.proxy.ProxyBuffer;

public class SimpleProxySelector extends ProxySelector {

	@Override
	public List<Proxy> select(URI uri) {
		List<Proxy> proxyList = ProxyBuffer.getInstance().getProxys();
		List<Proxy> select = new ArrayList<Proxy>(1);
		if (CollectionUtils.isEmpty(proxyList)) {
			select.add(Proxy.NO_PROXY);
		} else {
			Random rand = new Random();
			int index = rand.nextInt(proxyList.size());
			Proxy proxy = proxyList.get(index);
			select.add(proxy);
			ProxyBuffer.getInstance().addCall(proxy, uri.toString());
		}
		return select;
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		ProxyBuffer.getInstance().addError(new Proxy(Proxy.Type.HTTP, sa), uri.toString(), ioe);
	}

}
