package com.lezo.iscript.yeam.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.conn.DnsResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShuffleCacheDnsResolver implements DnsResolver {
	private static Logger logger = LoggerFactory.getLogger(ShuffleCacheDnsResolver.class);
	private Map<String, InetAddress[]> dnsMap;

	public ShuffleCacheDnsResolver() {
		dnsMap = new ConcurrentHashMap<String, InetAddress[]>();
	}

	@Override
	public InetAddress[] resolve(String host) throws UnknownHostException {
		InetAddress[] resolvedAddresses = dnsMap.get(host);
		if (resolvedAddresses == null) {
			resolvedAddresses = InetAddress.getAllByName(host);
			dnsMap.put(host, resolvedAddresses);
		}
		if (resolvedAddresses == null) {
			throw new UnknownHostException(host + " cannot be resolved");
		}
		doShuffle(resolvedAddresses);
		if (logger.isInfoEnabled()) {
			logger.info("Resolving " + host + " to " + Arrays.deepToString(resolvedAddresses));
		}
		return resolvedAddresses;
	}

	private void doShuffle(InetAddress[] resolvedAddresses) {
		Random rand = new Random();
		int index = rand.nextInt(resolvedAddresses.length);
		if (index == 0) {
			return;
		}
		InetAddress toAddress = resolvedAddresses[index];
		InetAddress curAddress = resolvedAddresses[0];
		resolvedAddresses[index] = curAddress;
		resolvedAddresses[0] = toAddress;
	}

}
