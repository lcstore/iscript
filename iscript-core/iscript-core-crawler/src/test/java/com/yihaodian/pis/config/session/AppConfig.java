package com.yihaodian.pis.config.session;

public class AppConfig {
	private String appkey;
	private String appsecret;
	private String sessionKey;

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	@Override
	public String toString() {
		final String split = "\t";
		return "" + appkey + split + appsecret + split + sessionKey;
	}
}
