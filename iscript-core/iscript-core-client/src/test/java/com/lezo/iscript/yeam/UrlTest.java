package com.lezo.iscript.yeam;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
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
		InetAddress[] intAddrs = InetAddress.getAllByName("www.ip138.com");
		for (InetAddress addr : intAddrs) {
			System.err.println(addr);
		}
	}

	@Test
	public void testPort() throws Exception {
		System.err.println(0xFFFF);
	}

	@Test
	public void testReadFile() throws Exception {
		List<String> lineList = FileUtils.readLines(new File("src/test/resources/data/tm.brandId.txt"), "UTF-8");
		Set<String> lineSet = new HashSet<String>(lineList);
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (String brand : lineSet) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(brand);
		}
		System.err.println("count:" + lineSet.size());
		System.err.println(sb);
	}
}
