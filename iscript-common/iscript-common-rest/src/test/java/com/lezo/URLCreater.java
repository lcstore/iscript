package com.lezo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class URLCreater {

	private static long currentTime = new Date().getTime() / 1000;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) throws Exception {
		createUrl(10246892, "7iha2uoousfacr207ge4e410sqgkphra", 1, "index.xml");
	}

	public static void createUrl(long tracker_u, String secret_key, Integer siteType, String xmlName) throws Exception {
		StringBuilder url = new StringBuilder();
		url.append("http://union.yhd.com/api/common/getXml.do");
		Long time = currentTime; /*
								 * sdf.parse("2013-05-01 01:00:00").getTime() /
								 * 1000;
								 */
		url.append("?time=").append(time);
		url.append("&siteType=").append(siteType);
		url.append("&tracker_u=").append(tracker_u);
		url.append("&xmlName=").append(xmlName);

		StringBuilder signStr = new StringBuilder();
		signStr.append("siteType=").append(siteType);
		signStr.append("time=").append(time);
		signStr.append("tracker_u=").append(tracker_u);
		signStr.append("xmlName=").append(xmlName);
		signStr.append(secret_key);
		String md5Str = MD5Support.MD5(signStr.toString());

		url.append("&sign=").append(md5Str);
		System.out.println(url);
	}
}
