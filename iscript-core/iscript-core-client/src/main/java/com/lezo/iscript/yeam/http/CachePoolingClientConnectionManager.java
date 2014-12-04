package com.lezo.iscript.yeam.http;

import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年12月3日
 */
public class CachePoolingClientConnectionManager extends PoolingClientConnectionManager {

	@Override
	protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
		// TODO Auto-generated method stub
		return super.createConnectionOperator(schreg);
	}
}
