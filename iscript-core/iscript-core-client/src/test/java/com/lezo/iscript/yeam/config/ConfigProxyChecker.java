package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.crawler.http.HttpClientUtils;
import com.lezo.iscript.scope.ScriptableUtils;
import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.http.HttpClientFactory;
import com.lezo.iscript.yeam.http.ProxySocketFactory;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxyChecker implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigProxyChecker.class);
	private DefaultHttpClient client;

	public ConfigProxyChecker() {
		this.client = HttpClientFactory.createHttpClient();
		this.client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(1, false));
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
		dataBean.getTargetList().add("ProxyAddrDto");

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
		Object idObject = task.get("id");
		Integer port = (Integer) task.get("port");
		String proxyIp = getHost(task);
		ProxyAddrDto tBean = new ProxyAddrDto();
		if (idObject != null) {
			tBean.setId(Long.valueOf(idObject.toString()));
		}
		findRegin(tBean, proxyIp, port);
//		findProxyType(tBean, proxyIp, port);
		DataBean rsBean = new DataBean();
		rsBean.getDataList().add(tBean);
		return rsBean;
	}

	private void findProxyType(ProxyAddrDto tBean, String proxyIp, Integer port) {
		int status = isHttpProxy(proxyIp, port);
		if (status == 1) {
			tBean.setType(ProxyAddrDto.TYPE_HTTP);
		} else if (status == -1) {
			status = isSocketProxy(proxyIp, port);
			if (status == 1) {
				tBean.setType(ProxyAddrDto.TYPE_SOCKET);
			}
		}
	}

	private int isHttpProxy(String proxyIp, Integer port) {
		String url = "http://doshome.com/yj/";
		HttpGet get = new HttpGet(url);
		try {
			HttpHost proxy = new HttpHost(proxyIp, port);
			get.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			// ExecutionContext.HTTP_PROXY_HOST
			String html = HttpClientUtils.getContent(client, get, "gbk");
			if (html != null && html.indexOf("DOS之家 2000-2015") > 0) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			logger.warn("isHttpProxy,cause:", e);
		} finally {
			get.abort();
		}
		return -1;
	}

	private int isSocketProxy(String proxyIp, Integer port) {
		String url = "http://doshome.com/yj/";
		HttpGet get = new HttpGet(url);
		try {
			InetSocketAddress socksaddr = new InetSocketAddress(proxyIp, port);
			get.getParams().setParameter(ProxySocketFactory.SOCKET_PROXY, new Proxy(Proxy.Type.SOCKS, socksaddr));
			String html = HttpClientUtils.getContent(client, get, "gbk");
			if (html != null && html.indexOf("DOS之家 2000-2015") > 0) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			logger.warn("isSocketProxy,cause:", e);
		} finally {
			get.abort();
		}
		return -1;
	}

	private void findRegin(ProxyAddrDto tBean, String proxyIp, Integer port) throws Exception {
		tBean.setIp(InetAddressUtils.inet_aton(proxyIp));
		tBean.setPort(port);
		if (isGetRegion(tBean, proxyIp, port)) {
			return;
		}

		HttpGet get = new HttpGet("http://opendata.baidu.com/api.php?query=" + proxyIp + "&co=&resource_id=6006&ie=utf8&oe=gbk&cb=op_aladdin_callback&format=json&tn=baidu");
		get.addHeader("Referer", "http://www.baidu.com/s?wd=ip&rsv_spt=1&issp=1&f=8&rsv_bp=0&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1&rsv_sug3=3&rsv_sug4=186&rsv_sug1=1&rsv_sug2=0&inputT=617");
		String html = HttpClientUtils.getContent(client, get);
		System.err.println(html);
		String source = "var op_aladdin_callback=function(data){ return data;};";
		source += html.replace("op_aladdin_callback", "var oData = op_aladdin_callback");
		source += " var sConfig = JSON.stringify(oData);";
		Context cx = Context.enter();
		Scriptable scope = ScriptableUtils.getJSONScriptable();
		cx.evaluateString(scope, source, "<region>", 0, null);
		String sConfig = Context.toString(ScriptableObject.getProperty(scope, "sConfig"));
		JSONObject regionObject = JSONUtils.getJSONObject(sConfig);
		JSONArray regionArray = JSONUtils.get(regionObject, "data");
		regionObject = regionArray.getJSONObject(0);
		String location = JSONUtils.getString(regionObject, "location");
		String[] txtArr = location.split("\\s");
		tBean.setRegionName(txtArr[0].trim());
		if (txtArr.length > 1) {
			tBean.setIspName(txtArr[1].trim());
		}
	}

	private boolean isGetRegion(ProxyAddrDto tBean, String proxyIp, Integer port) {
		HttpGet get = new HttpGet("http://www.ip138.com/ips138.asp?ip=" + proxyIp + "&action=2");
		get.addHeader("Referer", "http://www.ip138.com/");
		try {
			String html = HttpClientUtils.getContent(client, get, "gbk");
			Document dom = Jsoup.parse(html);
			Elements elements = dom.select("table td ul.ul1 li:contains(本站主数据)");
			if (elements.isEmpty()) {
				return false;
			}
			String sContent = elements.first().ownText();
			System.err.println(sContent);
			String sMark = "本站主数据：";
			int index = sContent.indexOf(sMark);
			sContent = sContent.substring(index + sMark.length());
			String[] txtArr = sContent.split("\\s");
			if (txtArr != null && txtArr.length >= 1) {
				tBean.setRegionName(txtArr[0].trim());
				if (txtArr.length > 1) {
					tBean.setIspName(txtArr[1].trim());
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!get.isAborted()) {
				get.abort();
			}
		}
		return false;
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

	private class ProxyAddrDto {
		public static final int TYPE_UNKNOWN = 0;
		public static final int TYPE_HTTP = 1;
		public static final int TYPE_SOCKET = 2;
		private Long id;
		private Long ip;
		private Integer port;
		private String regionName;
		private String ispName;
		private Integer type = TYPE_UNKNOWN;

		public Long getIp() {
			return ip;
		}

		public void setIp(Long ip) {
			this.ip = ip;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getRegionName() {
			return regionName;
		}

		public void setRegionName(String regionName) {
			this.regionName = regionName;
		}

		public String getIspName() {
			return ispName;
		}

		public void setIspName(String ispName) {
			this.ispName = ispName;
		}

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

	}
}
