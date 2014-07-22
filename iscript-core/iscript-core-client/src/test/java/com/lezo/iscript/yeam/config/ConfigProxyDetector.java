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
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.URLUtils;
import com.lezo.iscript.yeam.client.HardConstant;
import com.lezo.iscript.yeam.http.HttpRequestManager;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.simple.utils.ClientPropertiesUtils;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxyDetector implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigProxyDetector.class);
	private HttpRequestManager httpRequestManager;
	private List<String> detectUrls;
	private static final String DETECTOR = String.format("%s@%s", ClientPropertiesUtils.getProperty("name"),
			HardConstant.MAC_ADDR);

	public ConfigProxyDetector() {
		httpRequestManager = new HttpRequestManager();
		httpRequestManager.getClient().setRoutePlanner(null);
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
		DefaultHttpClient client = httpRequestManager.getClient();
		HttpHost proxy = new HttpHost(host, port);
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		JSONObject itemObject = new JSONObject();
		String url = getDetectUrl(task);
		HttpGet get = new HttpGet(url);
		long start = System.currentTimeMillis();
		int status = 0;
		try {
			// ExecutionContext.HTTP_PROXY_HOST
			HttpContext context = new BasicHttpContext();
			HttpResponse res = httpRequestManager.execute(get, context);
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
			get.abort();
		}
		long cost = System.currentTimeMillis() - start;
		JSONUtils.put(itemObject, "url", url);
		JSONUtils.put(itemObject, "domain", URLUtils.getRootHost(url));
		JSONUtils.put(itemObject, "cost", cost);
		JSONUtils.put(itemObject, "status", status);
		JSONUtils.put(itemObject, "detector", DETECTOR);
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
