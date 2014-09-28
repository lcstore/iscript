package com.lezo.iscript.crawler.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserAgentManager {
	private final static List<String> userAgentList = new ArrayList<String>();
	static {
		userAgentList.add("User-Agent	Mozilla/5.0 (Windows NT 5.1; rv:25.0) Gecko/20100101 Firefox/25.0");
		userAgentList
				.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET4.0C; .NET4.0E)");
		userAgentList
				.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C)");
		userAgentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
		userAgentList
				.add("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
		userAgentList
				.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");

		userAgentList
				.add("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
		userAgentList
				.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; msn OptimizedIE8;ZHCN; InfoPath.2; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; msn OptimizedIE8;ZHCN)");
		userAgentList.add("Mozilla/5.0 (Windows NT 5.1; rv:23.0) Gecko/20100101 Firefox/23.0");
		userAgentList
				.add("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
	}

	public static String getRadomUserAgent() {
		final int max = userAgentList.size();
		Random random = new Random();
		int index = random.nextInt(max);
		return userAgentList.get(index);
	}
}
