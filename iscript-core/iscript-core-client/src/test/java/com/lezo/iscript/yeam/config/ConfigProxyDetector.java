package com.lezo.iscript.yeam.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.URLUtils;
import com.lezo.iscript.yeam.http.HttpClientFactory;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxyDetector implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigProxyDetector.class);
	private List<String> detectUrls;

	public ConfigProxyDetector() {
		detectUrls = new ArrayList<String>();
		detectUrls.add("http://www.baidu.com/index.php?tn=19045005_6_pg");
		detectUrls.add("http://detail.tmall.com/item.htm?id=17031847966");
		detectUrls.add("http://item.jd.com/856850.html");
		detectUrls.add("http://detail.1688.com/offer/36970162715.html");
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		Integer port = (Integer) task.get("port");
		String host = getHost(task);

		JSONObject itemObject = new JSONObject();
		String url = getDetectUrl(task);
		HttpGet get = new HttpGet(url);
		long start = System.currentTimeMillis();
		int status = 0;
		DefaultHttpClient client = HttpClientFactory.createHttpClient();
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(2, false));
		try {
			HttpHost proxy = new HttpHost(host, port);
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			// ExecutionContext.HTTP_PROXY_HOST
			HttpContext context = new BasicHttpContext();
			HttpResponse res = client.execute(get, context);
			// HttpHost proxyHost = (HttpHost)
			// context.getAttribute(ExecutionContext.HTTP_PROXY_HOST);
			// JSONUtils.put(itemObject, "host", proxyHost.getHostName());
			// JSONUtils.put(itemObject, "port", proxyHost.getPort());
			int statusCode = res.getStatusLine().getStatusCode();
			String html = EntityUtils.toString(res.getEntity());
			status = getStatus(statusCode, html);
		} catch (Exception e) {
			status = 0;
			String msg = ExceptionUtils.getStackTrace(e);
			JSONUtils.put(itemObject, "ex", msg);
			logger.warn(String.format("detect url:%s,cause:%s", url, msg));
		} finally {
			if (get != null && !get.isAborted()) {
				get.abort();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		long cost = System.currentTimeMillis() - start;
		JSONUtils.put(itemObject, "url", url);
		JSONUtils.put(itemObject, "domain", URLUtils.getRootHost(url));
		JSONUtils.put(itemObject, "cost", cost);
		JSONUtils.put(itemObject, "status", status);
		JSONUtils.put(itemObject, "detector", HeaderUtils.CLIENT_NAME);
		return itemObject.toString();
	}

	private String getHost(TaskWritable task) {
		Object ipObject = task.get("ip");
		String ipString = ipObject.toString();
		int index = ipString.indexOf(".");
		if (index > 0) {
			return ipString;
		} else {
			Long ipLong = Long.valueOf(ipString);
			return InetAddressUtils.inet_ntoa(ipLong);
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
