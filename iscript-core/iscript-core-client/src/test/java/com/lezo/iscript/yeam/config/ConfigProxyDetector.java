package com.lezo.iscript.yeam.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpRequestManager;
import com.lezo.iscript.yeam.http.ProxyManager;
import com.lezo.iscript.yeam.http.SimpleProxyManager;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;
import com.sun.crypto.provider.RSACipher;

public class ConfigProxyDetector implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigProxyDetector.class);
	private HttpRequestManager httpRequestManager = new HttpRequestManager();
	private List<String> detectUrls;

	public ConfigProxyDetector() {
		detectUrls = new ArrayList<String>();
		detectUrls.add("http://www.baidu.com/index.php?tn=19045005_6_pg");
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		Long id = (Long) task.get("id");
		Integer port = (Integer) task.get("port");
		String host = getHost(task);
		ProxyManager proxyManager = new SimpleProxyManager();
		proxyManager.getEnableTrackers().clear();
		proxyManager.addTracker(id, host, port);
		httpRequestManager.setProxyManager(proxyManager);

		JSONObject itemObject = new JSONObject();
		String url = getDetectUrl(task);
		HttpGet get = new HttpGet(url);
		long start = System.currentTimeMillis();
		int status = 0;
		try {
			// ExecutionContext.HTTP_PROXY_HOST
			HttpContext context = new BasicHttpContext();
			HttpResponse res = httpRequestManager.execute(get, context);
			int statusCode = res.getStatusLine().getStatusCode();
 			String html = EntityUtils.toString(res.getEntity());
			status = getStatus(statusCode, html);
		} catch (Exception e) {
			status = 0;
			String msg = ExceptionUtils.getStackTrace(e);
			JSONUtils.put(itemObject, "ex", msg);
			logger.warn(String.format("detect url:%s,cause:%s", url, msg));
			get.abort();
		}
		long cost = System.currentTimeMillis() - start;
		JSONUtils.put(itemObject, "url", url);
		JSONUtils.put(itemObject, "cost", cost);
		JSONUtils.put(itemObject, "status", status);
		return itemObject.toString();
	}

	private String getHost(TaskWritable task) {
		Object ipObject = task.get("ip");
		if (ipObject instanceof String) {
			return ipObject.toString();
		} else if (ipObject instanceof Long) {
			Long ipLong = (Long) ipObject;
			return InetAddressUtils.inet_ntoa(ipLong);
		} else {
			throw new IllegalArgumentException("unkonwn ip data type:" + ipObject.getClass().getName());
		}
	}

	private int getStatus(int statusCode, String html) {
		if (statusCode < 200 || statusCode >= 300) {
			return 0;
		}
		return 1;
	}

	private String getDetectUrl(TaskWritable task) {
		Object urlObject = task.get("url");
		if (urlObject != null) {
			return urlObject.toString();
		}
		Random rand = new Random();
		int index = rand.nextInt(detectUrls.size());
		return detectUrls.get(index);
	}
}
