package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;

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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.URLUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.http.HttpClientFactory;
import com.lezo.iscript.yeam.http.ProxySocketFactory;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxyDetector implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigProxyDetector.class);
	private DefaultHttpClient client;

	public ConfigProxyDetector() {
		this.client = HttpClientFactory.createHttpClient();
		this.client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(2, false));
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
		Integer port = (Integer) task.get("port");
		Integer type = (Integer) task.get("proxyType");
		type = type == null ? 0 : type;
		String proxyIp = getHost(task);
		String url = getDetectUrl(task);
		ProxyDetectDto tBean = null;
		if (2 == type) {
			tBean = getSocketProxyDetectDto(url, proxyIp, port);
			tBean.setType(type);
		} else if (1 == type) {
			tBean = getHttpProxyDetectDto(url, proxyIp, port);
			tBean.setType(type);
		} else {
			tBean = getHttpProxyDetectDto(url, proxyIp, port);
			if (tBean.getStatus() == 1 || tBean.getRemark() != null) {
				tBean.setType(1);
			} else if (tBean.getRemark() == null) {
				tBean = getSocketProxyDetectDto(url, proxyIp, port);
				if (tBean.getStatus() == 1) {
					tBean.setType(2);
				}
			}
		}
		DataBean rsBean = new DataBean();
		rsBean.getDataList().add(tBean);
		return rsBean;
	}

	private ProxyDetectDto getSocketProxyDetectDto(String url, String proxyIp, Integer port) {
		long start = System.currentTimeMillis();
		HttpGet get = newSocketProxyGet(url, proxyIp, port);
		String domain = URLUtils.getRootHost(url);

		ProxyDetectDto tBean = new ProxyDetectDto();
		tBean.setIp(InetAddressUtils.inet_aton(proxyIp));
		tBean.setPort(port);
		tBean.setDetector(HeaderUtils.CLIENT_NAME);
		tBean.setDomain(domain);
		tBean.setUrl(url);
		try {
			HttpContext context = new BasicHttpContext();
			HttpResponse res = client.execute(get, context);
			fillStatus(tBean, res, domain);
		} catch (Exception e) {
			logger.warn("SocketProxy url:" + url + ",proxy:" + proxyIp + ":" + port + ",cause:", e);
		} finally {
			if (get != null && !get.isAborted()) {
				get.abort();
			}
		}
		long cost = System.currentTimeMillis() - start;
		tBean.setCurCost(cost);
		return tBean;
	}

	private ProxyDetectDto getHttpProxyDetectDto(String url, String proxyIp, Integer port) {
		long start = System.currentTimeMillis();
		HttpGet get = newHttpProxyGet(url, proxyIp, port);
		String domain = URLUtils.getRootHost(url);
		ProxyDetectDto tBean = new ProxyDetectDto();
		tBean.setIp(InetAddressUtils.inet_aton(proxyIp));
		tBean.setPort(port);
		tBean.setDetector(HeaderUtils.CLIENT_NAME);
		tBean.setDomain(domain);
		tBean.setUrl(url);
		try {
			HttpContext context = new BasicHttpContext();
			HttpResponse res = client.execute(get, context);
			fillStatus(tBean, res, domain);
		} catch (Exception e) {
			logger.warn("HttpProxy url:" + url + ",proxy:" + proxyIp + ":" + port + ",cause:", e);
		} finally {
			if (get != null && !get.isAborted()) {
				get.abort();
			}
		}
		long cost = System.currentTimeMillis() - start;
		tBean.setCurCost(cost);
		return tBean;
	}

	private void fillStatus(ProxyDetectDto tBean, HttpResponse res, String domain) throws Exception {
		int statusCode = res.getStatusLine().getStatusCode();
		String html = EntityUtils.toString(res.getEntity());
		if (statusCode < 200 || statusCode >= 300) {
			tBean.setRemark("code:" + statusCode + ",html:" + html);
			tBean.setStatus(0);
			limitRemarkLen(tBean);
			return;
		}
		if (html != null && html.indexOf(domain) > 0) {
			tBean.setVerifyStatus(1);
			tBean.setStatus(1);
			return;
		} else {
			if (html != null) {
				tBean.setRemark(html);
			} else {
				tBean.setRemark("html is null");
			}
			tBean.setVerifyStatus(-1);
			limitRemarkLen(tBean);
		}
		tBean.setStatus(0);
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

	}
}
