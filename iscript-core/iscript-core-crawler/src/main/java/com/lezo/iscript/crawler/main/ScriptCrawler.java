package com.lezo.iscript.crawler.main;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

import com.lezo.iscript.crawler.http.HttpBrowserManager;
import com.lezo.iscript.crawler.http.SimpleHttpBrowser;
import com.lezo.iscript.crawler.http.SimpleResponseHandler;

public class ScriptCrawler {
	private static Logger log = Logger.getLogger(ScriptCrawler.class);

	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			args = new String[] { "http://www.baidu.com/" };
		}
		String name = "smarter";
		String url = args[0];
		SimpleHttpBrowser browser = HttpBrowserManager.buildBrowser(name);
		HttpUriRequest request = new HttpGet(url);
		ResponseHandler<String> responseHandler = new SimpleResponseHandler();
		try {
			String result = browser.execute(request, responseHandler);
			String msg = "result for url:" + url + ",rs:" + result;
			log.info(msg);
			System.out.println(msg);
		} catch (Exception e) {
			log.error("when request:" + url + ",cause:", e);
		}
	}

}
