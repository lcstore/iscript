package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.URLUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.http.HttpClientFactory;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.http.ProxySocketFactory;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxyDetector implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigProxyDetector.class);
	private DefaultHttpClient client;
	private Map<String, String> domainValueMap;

	public ConfigProxyDetector() {
		this.client = HttpClientFactory.createHttpClient();
		this.client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(2, false));

		this.domainValueMap = new HashMap<String, String>();
		domainValueMap.put("mi.com", "http://www.mi.com/favicon.ico");
		domainValueMap.put("baidu.com", "http://www.baidu.com/duty/");
		domainValueMap.put("tmall.com", "J_ItemList");
		domainValueMap.put("yhd.com", "http://d9.yihaodianimg.com/N02/M02");
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		DataBean dataBean = getDataObject(task);
		return convert2TaskCallBack(dataBean, task);
	}

	private String convert2TaskCallBack(DataBean dataBean, TaskWritable task) throws Exception {
		dataBean.getTargetList().add("ProxyDetectDto");

		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, dataBean);
		String dataString = writer.toString();

		JSONObject returnObject = new JSONObject();
		JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, JSONUtils.EMPTY_JSONOBJECT);
		JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
		return returnObject.toString();
	}

	private DataBean getDataObject(TaskWritable task) throws Exception {
		Integer port = getOrSecond(task, "port", "proxyPort");
		Integer type = (Integer) task.get("proxyType");
		type = type == null ? 0 : type;
		String proxyHost = getHost(task);
		String url = getDetectUrl(task);
		ProxyDetectDto tBean = new ProxyDetectDto();
		tBean.setStartMills(System.currentTimeMillis());
		tBean.setIp(InetAddressUtils.inet_aton(proxyHost));
		tBean.setPort(port);
		tBean.setDetector(HeaderUtils.CLIENT_NAME);
		tBean.setDomain(URLUtils.getRootHost(url));
		tBean.setUrl(url);
		tBean.setType(type);
		List<Integer> typeList = getTypeList(type);
		for (int index : typeList) {
			VerifyMsg msg = doVerify(url, proxyHost, port, index, getCheckValue(url));
			tBean.setCurCost(msg.getCost());
			int firstCode = msg.getCode();
			switch (firstCode) {
			case 1: {
				url = "http://www.mi.com/";
				msg = doVerify(url, proxyHost, port, index, getCheckValue(url));
				break;
			}
			default:
				break;
			}
			if (firstCode != 0) {
				tBean.setStatus(1);
				tBean.setVerifyStatus(msg.getCode());
				tBean.setRemark(msg.getCause());
				tBean.setType(index);
				break;
			}
		}
		limitRemarkLen(tBean);
		tBean.setEndMills(System.currentTimeMillis());
		DataBean rsBean = new DataBean();
		rsBean.getDataList().add(tBean);
		return rsBean;
	}

	private List<Integer> getTypeList(Integer type) {
		List<Integer> typeList = new ArrayList<Integer>();
		if (1 == type || 2 == type) {
			typeList.add(type);
		} else {
			typeList.add(1);
			typeList.add(2);
		}
		return typeList;
	}

	private VerifyMsg doVerify(String url, String proxyHost, Integer port, Integer type, String checkValue) {
		HttpGet get = HttpClientUtils.createHttpGet(url, proxyHost, port, type);
		int index = url.indexOf("#");
		index = index < 0 ? url.length() : index;
		String referUrl = url.substring(0, index);
		get.addHeader("Referer", referUrl);
		get.addHeader("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
		get.addHeader("Cookie", "ATS_PASS=0");
		VerifyMsg msg = new VerifyMsg();
		long start = System.currentTimeMillis();
		try {
			HttpResponse res = client.execute(get);
			int statusCode = res.getStatusLine().getStatusCode();
			if (statusCode < 200 || statusCode >= 300) {
				msg.setCode(0);
			} else {
				HttpEntity entity = res.getEntity();
				byte[] dataBytes = EntityUtils.toByteArray(entity);
				String charsetName = HttpClientUtils.getCharsetOrDefault(entity.getContentType(), dataBytes, "UTF-8");
				String html = new String(dataBytes, charsetName);
//				FileUtils.writeStringToFile(new File("src/test/resources/data/" + res.hashCode() + ".html"), html);
				msg.setCode(-1);
				if (html != null && html.contains(checkValue)) {
					msg.setCode(1);
				}
				if (-1 == msg.getCode()) {
					Document dom = Jsoup.parse(html, url);
					Elements titleEls = dom.select("head title");
					msg.setCause(titleEls.isEmpty() ? dom.text() : titleEls.text());
				}
			}
		} catch (Exception e) {
			logger.warn("url:" + url + ",proxy:" + proxyHost + ":" + port + ",type:" + type + ",cause:", e);
		} finally {
			long cost = System.currentTimeMillis() - start;
			msg.setCost(cost);
			if (get != null && !get.isAborted()) {
				get.abort();
			}
		}
		return msg;
	}

	private String getCheckValue(String url) {
		String domain = URLUtils.getRootHost(url);
		String checkValue = domainValueMap.get(domain);
		if (checkValue == null) {
			checkValue = domain;
			logger.warn("get empty CheckValue.use:" + checkValue);
		}
		return checkValue;
	}

	private void limitRemarkLen(ProxyDetectDto tBean) {
		if (tBean.getRemark() == null) {
			return;
		}
		int maxLen = 95;
		String html = tBean.getRemark();
		int len = html.length() > maxLen ? maxLen : html.length();
		html = html.substring(0, len);
		tBean.setRemark(html);
	}

	private String getHost(TaskWritable task) {
		Object ipObject = getOrSecond(task, "ip", "proxyHost");
		String ipString = ipObject.toString();
		int index = ipString.indexOf(".");
		if (index > 0) {
			return ipString;
		} else {
			Long ipLong = Long.valueOf(ipString);
			return InetAddressUtils.inet_ntoa(ipLong);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getOrSecond(TaskWritable task, String key, String second) {
		Object value = task.get(key);
		return (T) (value == null ? task.get(second) : value);
	}

	private String getDetectUrl(TaskWritable task) {
		Object urlObject = task.get("url");
		if (urlObject != null) {
			return urlObject.toString();
		} else {
			return "http://www.baidu.com/";
		}
	}

	public HttpGet newHttpProxyGet(String url, String proxyIp, Integer port) {
		HttpGet get = new HttpGet(url);
		HttpHost proxy = new HttpHost(proxyIp, port);
		get.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		return get;
	}

	public HttpGet newSocketProxyGet(String url, String proxyIp, Integer port) {
		HttpGet get = new HttpGet(url);
		InetSocketAddress socksaddr = new InetSocketAddress(proxyIp, port);
		get.getParams().setParameter(ProxySocketFactory.SOCKET_PROXY, new Proxy(Proxy.Type.SOCKS, socksaddr));
		return get;
	}

	@SuppressWarnings("unused")
	private class ProxyDetectDto {
		private Long ip;
		private int port;
		private String domain;
		private String url;
		private String detector;
		private Long curCost;
		private int status = 0;

		private Integer verifyStatus = 0;
		private String remark;
		private Integer type = 0;
		private long startMills;
		private long endMills;

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

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public Integer getVerifyStatus() {
			return verifyStatus;
		}

		public void setVerifyStatus(Integer verifyStatus) {
			this.verifyStatus = verifyStatus;
		}

		public long getStartMills() {
			return startMills;
		}

		public void setStartMills(long startMills) {
			this.startMills = startMills;
		}

		public long getEndMills() {
			return endMills;
		}

		public void setEndMills(long endMills) {
			this.endMills = endMills;
		}

	}

	private class VerifyMsg {
		private int code;
		private long cost;
		private String cause;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public long getCost() {
			return cost;
		}

		public void setCost(long cost) {
			this.cost = cost;
		}

		public String getCause() {
			return cause;
		}

		public void setCause(String cause) {
			this.cause = cause;
		}
	}
}
