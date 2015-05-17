package com.lezo.iscript.rest.http;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年12月3日
 */
public class CachePoolingClientConnectionManager extends PoolingClientConnectionManager {

	public CachePoolingClientConnectionManager(SchemeRegistry schreg, DnsResolver dnsResolver) {
		super(schreg, dnsResolver);
	}

	public CachePoolingClientConnectionManager(SchemeRegistry schemeRegistry, long timeToLive, TimeUnit tunit, DnsResolver dnsResolver) {
		super(schemeRegistry, timeToLive, tunit, dnsResolver);
	}

	@Override
	protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
		// TODO Auto-generated method stub
		return super.createConnectionOperator(schreg);
	}
}
