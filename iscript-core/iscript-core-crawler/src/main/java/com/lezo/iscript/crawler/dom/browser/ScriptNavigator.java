package com.lezo.iscript.crawler.dom.browser;

public class ScriptNavigator {
	private String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36";
	private String platform = "Win32";
	private String appVersion = "5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36";
	private String appName = "Netscape";
	private String appCodeName = "Mozilla";
	private String vendor = "";
	private String product = "Gecko";

	public ScriptNavigator() {
		super();
	}

	public String getPlatform() {
		return platform;
	}

	public String getUserAgent() {
		return this.userAgent;
	}

	public String getVendor() {
		return this.vendor;
	}

	public String getProduct() {
		return this.product;
	}

	public boolean javaEnabled() {
		// True always?
		return true;
	}

	private MimeTypesCollection mimeTypes;

	public MimeTypesCollection getMimeTypes() {
		synchronized (this) {
			MimeTypesCollection mt = this.mimeTypes;
			if (mt == null) {
				mt = new MimeTypesCollection();
				this.mimeTypes = mt;
			}
			return mt;
		}
	}

	public class MimeTypesCollection {
		// Class must be public to allow JavaScript access
		public int getLength() {
			return 0;
		}

		public Object item(int index) {
			return null;
		}

		public Object namedItem(String name) {
			return null;
		}
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppCodeName() {
		return appCodeName;
	}

	public void setAppCodeName(String appCodeName) {
		this.appCodeName = appCodeName;
	}

	public void setMimeTypes(MimeTypesCollection mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

}