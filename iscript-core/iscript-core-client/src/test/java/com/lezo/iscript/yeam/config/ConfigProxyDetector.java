package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
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
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.URLUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientFactory;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxyDetector implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigProxyDetector.class);
	private static final String EMTPY_RESULT = new JSONObject().toString();
	private List<String> detectUrls;

	public ConfigProxyDetector() {
		detectUrls = new ArrayList<String>();
		detectUrls.add("http://www.baidu.com/index.php?tn=19045005_6_pg");
		detectUrls.add("http://detail.tmall.com/item.htm?id=17031847966");
		detectUrls.add("http://item.jd.com/856850.html");
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject gObject = new JSONObject();
		JSONObject itemObject = getDataObject(task, gObject);
		System.out.println(itemObject);
		JSONUtils.put(gObject, "rs", itemObject.toString());
		doCollect(gObject, task);
		return EMTPY_RESULT;
	}

	private void doCollect(JSONObject gObject, TaskWritable task) {
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);

		JSONArray tArray = new JSONArray();
		tArray.put("ProxyDetectDto");
		JSONUtils.put(argsObject, "target", tArray);
		JSONUtils.put(gObject, "args", argsObject);

		System.err.println("gObject:" + gObject);
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}

	private JSONObject getDataObject(TaskWritable task, JSONObject gObject) throws Exception {
		Integer port = (Integer) task.get("port");
		String host = getHost(task);
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
			JSONUtils.put(gObject, "ex", msg);
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
		JSONObject argsObject = new JSONObject(task.getArgs());
		ProxyDetectDto tBean = new ProxyDetectDto();
		tBean.setIp(InetAddressUtils.inet_aton(host));
		tBean.setPort(JSONUtils.getInteger(argsObject, "port"));

		tBean.setDetector(HeaderUtils.CLIENT_NAME);
		tBean.setStatus(status);
		tBean.setCurCost(cost);
		tBean.setDomain(URLUtils.getRootHost(url));
		tBean.setUrl(url);
		ResultBean rsBean = new ResultBean();
		rsBean.getDataList().add(tBean);
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
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

	private final class ResultBean {
		private List<Object> dataList = new ArrayList<Object>();
		private List<Object> nextList = new ArrayList<Object>();

		public List<Object> getDataList() {
			return dataList;
		}

		public void setDataList(List<Object> dataList) {
			this.dataList = dataList;
		}

		public List<Object> getNextList() {
			return nextList;
		}

		public void setNextList(List<Object> nextList) {
			this.nextList = nextList;
		}

	}

	private class ProxyDetectDto {
		private Long ip;
		private int port;
		private String domain;
		private String url;
		private String detector;
		private Long curCost;
		private int status = 0;

		public Long getIp() {
			return ip;
		}

		public void setIp(Long ip) {
			this.ip = ip;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getDetector() {
			return detector;
		}

		public void setDetector(String detector) {
			this.detector = detector;
		}

		public Long getCurCost() {
			return curCost;
		}

		public void setCurCost(Long curCost) {
			this.curCost = curCost;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

	}
}
