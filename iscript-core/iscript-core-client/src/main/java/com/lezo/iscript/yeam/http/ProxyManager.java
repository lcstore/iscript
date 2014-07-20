package com.lezo.iscript.yeam.http;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

public interface ProxyManager {
	void trackFail(URI uri, SocketAddress sa, IOException ioe);

	List<ProxyTracker> getEnableTrackers();

	List<ProxyTracker> getDiableTrackers();

	void addTracker(Long id, String host, int port);

}
