package com.lezo.iscript.rest.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.conn.DnsResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShuffleCacheDnsResolver implements DnsResolver {
	private static Logger logger = LoggerFactory.getLogger(ShuffleCacheDnsResolver.class);
	private ConcurrentHashMap<String, InetAddress[]> dnsMap;

	public ShuffleCacheDnsResolver() {
		dnsMap = new ConcurrentHashMap<String, InetAddress[]>();
	}

	@Override
	public InetAddress[] resolve(String host) throws UnknownHostException {
		InetAddress[] domainAddrs = dnsMap.get(host);
		if (domainAddrs == null) {
			domainAddrs = InetAddress.getAllByName(host);
			dnsMap.put(host, domainAddrs);
		}
		if (domainAddrs == null) {
			throw new UnknownHostException(host + " cannot be resolved");
		}
		doShuffle(domainAddrs);
		if (logger.isDebugEnabled()) {
			logger.debug("Resolving " + host + " to " + Arrays.deepToString(domainAddrs));
		}
		return domainAddrs;
	}

	private void doShuffle(InetAddress[] inetAddrs) {
		if (inetAddrs == null || inetAddrs.length < 2) {
			return;
		}
		Random rand = new Random();
		int index = rand.nextInt(inetAddrs.length);
		if (index == 0) {
			return;
		}
		InetAddress toAddress = inetAddrs[index];
		InetAddress curAddress = inetAddrs[0];
		inetAddrs[index] = curAddress;
		inetAddrs[0] = toAddress;
	}

}
