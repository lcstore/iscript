package com.lezo.iscript.yeam.crawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.params.HttpProtocolParams;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.script.CommonContext;
import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class JdBarCodeSimilar implements ConfigParser {
	private static Logger log = Logger.getLogger(JdBarCodeSimilar.class);
	private DefaultHttpClient client;
	private Scriptable scope;

	public JdBarCodeSimilar() {
		try {
			ScriptableObject parent = CommonContext.getCommonScriptable();
			scope = Context.enter().initStandardObjects(parent);
			client = HttpClientUtils.createHttpClient();
			ProxySelector prosel = new ProxySelector() {
				private List<Proxy> proxyList;

				@Override
				public List<Proxy> select(URI uri) {
					if (proxyList == null) {
						proxyList = new ArrayList<Proxy>();
						proxyList.add(createProxy("122.96.59.102", 843));
						proxyList.add(createProxy("89.46.101.122", 8089));
						proxyList.add(createProxy("98.143.148.82", 7808));
						proxyList.add(createProxy("5.135.58.225", 3127));
						proxyList.add(createProxy("23.89.198.161", 7808));
					}
					Random rand = new Random();
					int index = rand.nextInt(proxyList.size());
					List<Proxy> select = new ArrayList<Proxy>();
					select.add(proxyList.get(index));
					return select;
				}

				private Proxy createProxy(String host, int port) {
					InetAddress addr = null;
					try {
						addr = InetAddress.getByName(host);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SocketAddress sa = new InetSocketAddress(addr, port);
					return new Proxy(Proxy.Type.HTTP, sa);
				}

				@Override
				public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
					// TODO Auto-generated method stub

				}
			};
			SchemeRegistry schreg = client.getConnectionManager().getSchemeRegistry();
			HttpRoutePlanner routePlanner = new ProxySelectorRoutePlanner(schreg, prosel);
			client.setRoutePlanner(routePlanner);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Context.exit();
		}
	}

	public String getName() {
		return "jd-barCode";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String userAgent = "Dalvik/1.6.0 (Linux; U; Android 4.1.1; MI 2 MIUI/JLB34.0)";
		HttpProtocolParams.setUserAgent(client.getParams(), userAgent);
		JSONObject rs = new JSONObject();
		JSONUtils.put(rs, "args", JSONUtils.getJSONObject(task.getArgs()));
		String sUrl = getSimilarUrl(task);
		HttpGet get = new HttpGet(sUrl);
		get.addHeader("refer", "http://gw.m.360buy.com");
		String html = HttpClientUtils.getContent(client, get);
		JSONUtils.put(rs, "rs", html);
		Context.exit();
		return rs.toString();
	}

	private String getSimilarUrl(TaskWritable task) {
		String barCode = get(task.getArgs(), "barCode");
		String url = "http://gw.m.360buy.com/client.action?functionId=wareIdByBarCodeList&uuid="
				+ getRandomUid()
				+ "-acf7f34353f1&clientVersion=3.6.3&client=android&d_brand=Xiaomi&d_model=MI2&osVersion=4.1.1&screen=1280*720&partner=jingdong&networkType=wifi&area=2_2841_0_0&sv=1&st="
				+ System.currentTimeMillis();
		String paramString = "{\"barcode\":\"" + barCode + "\"}";
		try {
			url += "&body=" + URLEncoder.encode(paramString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}

	public static String getRandomUid() {
		Integer randomDataLong = new Random().nextInt(10000);
		String head = "860308028232581";
		String ranString = head + randomDataLong.toString();
		return ranString.substring(ranString.length() - head.length());
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Map<String, Object> map, String key) {
		if (key == null) {
			return null;
		}
		Object valueObject = map.get(key);
		if (valueObject == null) {
			return null;
		}
		return (T) valueObject;
	}

	public static Integer getInteger(Map<String, Object> map, String key) {
		return get(map, key);
	}

}
