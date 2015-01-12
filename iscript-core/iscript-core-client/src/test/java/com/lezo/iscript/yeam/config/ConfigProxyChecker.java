package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.crawler.http.HttpClientUtils;
import com.lezo.iscript.scope.ScriptableUtils;
import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientFactory;
import com.lezo.iscript.yeam.http.ProxySocketFactory;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxyChecker implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigProxyChecker.class);
	private DefaultHttpClient client;
	private static final String EMTPY_RESULT = new JSONObject().toString();

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
		tArray.put("ProxyAddrDto");
		JSONUtils.put(argsObject, "target", tArray);
		JSONUtils.put(gObject, "args", argsObject);

		System.err.println("gObject:" + gObject);
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}

	private JSONObject getDataObject(TaskWritable task, JSONObject gObject) throws Exception {
		Long id = (Long) task.get("id");
		Integer port = (Integer) task.get("port");
		String proxyIp = getHost(task);
		ProxyAddrDto tBean = new ProxyAddrDto();
		tBean.setId(id);
		findRegin(tBean, proxyIp, port);
		findProxyType(tBean, proxyIp, port);
		ResultBean rsBean = new ResultBean();
		rsBean.getDataList().add(tBean);
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	private void findProxyType(ProxyAddrDto tBean, String proxyIp, Integer port) {
		if (isHttpProxy(proxyIp, port)) {
			tBean.setType(ProxyAddrDto.TYPE_HTTP);
			return;
		}
		if (isSocketProxy(proxyIp, port)) {
			tBean.setType(ProxyAddrDto.TYPE_SOCKET);
			return;
		}
	}

	private boolean isHttpProxy(String proxyIp, Integer port) {
		String url = "http://doshome.com/yj/";
		HttpGet get = new HttpGet(url);
		try {
			HttpHost proxy = new HttpHost(proxyIp, port);
			get.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			// ExecutionContext.HTTP_PROXY_HOST
			HttpContext context = new BasicHttpContext();
			HttpResponse res = client.execute(get, context);
			if (res.getStatusLine().getStatusCode() == 200) {
				return true;
			}
		} catch (Exception e) {
			logger.warn("isHttpProxy,cause:", e);
		} finally {
			get.abort();
		}
		return false;
	}

	private boolean isSocketProxy(String proxyIp, Integer port) {
		String url = "http://doshome.com/yj/";
		HttpGet get = new HttpGet(url);
		try {
			InetSocketAddress socksaddr = new InetSocketAddress(proxyIp, port);
			get.getParams().setParameter(ProxySocketFactory.SOCKET_PROXY, new Proxy(Proxy.Type.SOCKS, socksaddr));
			// ExecutionContext.HTTP_PROXY_HOST
			HttpContext context = new BasicHttpContext();
			HttpResponse res = client.execute(get, context);
			if (res.getStatusLine().getStatusCode() == 200) {
				return true;
			}
		} catch (Exception e) {
			logger.warn("isSocketProxy,cause:", e);
		} finally {
			get.abort();
		}
		return false;
	}

	private void findRegin(ProxyAddrDto tBean, String proxyIp, Integer port) throws Exception {
		tBean.setIp(InetAddressUtils.inet_aton(proxyIp));
		tBean.setPort(port);
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
