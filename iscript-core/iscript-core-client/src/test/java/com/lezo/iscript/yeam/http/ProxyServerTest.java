package com.lezo.iscript.yeam.http;

import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;

/**
 * 代理池服务，全球各地的IP
 * 
 * @author lezo
 *
 */
public class ProxyServerTest {
	public static void main(String[] args) throws Exception {
		HttpHost proxy = new HttpHost("zproxy.luminati.io", 22225);
		String res = Executor.newInstance().auth(proxy, "lum-customer-CUSTOMER-zone-lcstore", "lu@9Lezo")
				.execute(Request.Get("http://www.telize.com/geoip").viaProxy(proxy)).returnContent().asString();
		System.out.println(res);
	}
}
