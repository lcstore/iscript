package com.lezo.iscript;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.lezo.iscript.crawler.utils.HttpClientUtils;

public class WccMain {

	@Test
	public void test() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpUriRequest get = new HttpGet(
				"http://click.linktech.cn?m=shangpinwap&a=A100165669&u_id=&l=99999&l_cd1=A&l_cd2=s&tu=http%3A%2F%2Fm.shangpin.com%2Fwomen%2Fproduct%2F07433820");
		get.addHeader("Accept-Encoding", "gzip, deflate");
		get.addHeader("Charset", "gzip, deflate");
		String html = HttpClientUtils.getContent(client, get);
		System.out.println(html);
	}
}
