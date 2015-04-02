package com.lezo.iscript.yeam;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import sun.util.calendar.ZoneInfo;

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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		System.err.println(sdf.format(new Date()));
		// for(String id: TimeZone.getAvailableIDs()){
		// System.err.println("id="+id);
		// }
		Calendar c = Calendar.getInstance(ZoneInfo.getTimeZone("Etc/GMT+8"));
		// c.setTimeZone(ZoneInfo.getTimeZone("Asia/Harbin"));
		// c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		// c.setTimeZone(ZoneInfo.getTimeZone("HST"));
		// TimeZone.setDefault(ZoneInfo.getTimeZone("Asia/Shanghai"));

		TimeZone timeZone1 = TimeZone.getTimeZone("America/Los_Angeles");
		TimeZone timeZone2 = TimeZone.getTimeZone("Europe/Copenhagen");

		Calendar calendar = new GregorianCalendar();
		calendar = Calendar.getInstance();

		long timeCPH = calendar.getTimeInMillis();
		System.out.println("timeCPH  = " + timeCPH);
		System.out.println("hour     = " + calendar.get(Calendar.HOUR_OF_DAY));

		calendar.setTimeZone(timeZone1);

		long timeLA = calendar.getTimeInMillis();
		System.out.println("timeLA   = " + timeLA);
		System.out.println("hour     = " + calendar.get(Calendar.HOUR_OF_DAY));
	}

	@Test
	public void testReadFile() throws Exception {
		List<String> lineList = FileUtils.readLines(new File("src/test/resources/data/tm.brandId.txt"), "UTF-8");
		Set<String> lineSet = new HashSet<String>(lineList);
		StringBuilder sb = new StringBuilder();
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
