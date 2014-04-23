package com.lezo.rest.jos;

public class JosConfig {
	private String appKey;
	private String appSecret;
	private String accessToken = "";
	private String serverUrl = "http://gw.api.360buy.com/routerjson";

	public JosConfig(String appKey, String appSecret) {
		super();
		this.appKey = appKey;
		this.appSecret = appSecret;
	}

	public JosConfig(String appKey, String appSecret, String accessToken) {
		super();
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.accessToken = accessToken;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
}
