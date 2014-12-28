package com.lezo.iscript.yeam;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.junit.Test;

public class UrlTest {

	@Test
	public void testUrl() {
		String url = "http://www.yhd.com/ctg/searchPage/c21266-0/b/a-s2-v0-p1-price-d0-f0-m1-rt0-pid-mid0-k%25E7%25A7%2591%25E6%25B2%2583%25E6%2596%25AF|/?callback=jsonp1409631030727";
		try {
			URL oUrl = new URL(url);
			System.out.println(url.substring(130));
			System.out.println(url.replace("|/?", "?"));
			System.out.println(oUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testDns() throws Exception {
		InetAddress[] intAddrs = InetAddress.getAllByName("e.yhd.com");
		for(InetAddress addr:intAddrs){
			System.err.println(addr);
		}
	}
}
