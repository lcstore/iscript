package com.lezo.rest.jos;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.lezo.rest.SignBuildable;

public class JosRestClient {
	private static final String REQUEST_ENCODING = "UTF-8";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SignBuildable builder = new JosSignBuilder();
	private JosConfig config;

	public JosRestClient(JosConfig config) {
		super();
		this.config = config;
	}

	public JosRestClient(String appKey, String appSecret, String accessToken) {
		super();
		this.config = new JosConfig(appKey, appSecret, accessToken);
	}

	public String execute(String method, String clientArgs) throws Exception {
		// 默认参数
		String serverUrl = config.getServerUrl();
		String timestamp = sdf.format(new Date());
		String format = "json";
		String version = "2.0";
		return execute(serverUrl, method, clientArgs, timestamp, format, version);
	}

	public String execute(String serverUrl, String method, String clientArgs, String timestamp, String format,
			String version) throws Exception {
		// 构造jos rest 参数
		Map<String, Object> inMap = new HashMap<String, Object>();
		inMap.put("access_token", this.config.getAccessToken());
		inMap.put("app_key", this.config.getAppKey());
		inMap.put("app_secret", this.config.getAppSecret());
		inMap.put("timestamp", timestamp);
		inMap.put("format", format);
		inMap.put("v", version);
		inMap.put("method", method);
		inMap.put("360buy_param_json", clientArgs);
		// 获取签名
		String sign = builder.getSign(inMap);
		inMap.put("sign", sign);
		inMap.remove("app_secret");

		// 拼接参数列表
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		for (Entry<String, Object> entry : inMap.entrySet()) {
			paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
		}
		return execute(serverUrl, paramList);
	}

	public String execute(String serverUrl, List<NameValuePair> paramList) throws Exception {
		HttpGet httpget = new HttpGet(serverUrl);
		DefaultHttpClient hc = new DefaultHttpClient();
		String params = EntityUtils.toString(new UrlEncodedFormEntity(paramList, REQUEST_ENCODING));
		httpget.setURI(new URI(httpget.getURI().toString() + "?" + params));
		HttpResponse response = hc.execute(httpget);
		String content = null;
		if (response == null) {
			return content;
		}
		HttpEntity entity = response.getEntity();
		content = EntityUtils.toString(entity);
		return content;
	}

	public JosConfig getConfig() {
		return config;
	}

	public void setConfig(JosConfig config) {
		this.config = config;
	}

}
