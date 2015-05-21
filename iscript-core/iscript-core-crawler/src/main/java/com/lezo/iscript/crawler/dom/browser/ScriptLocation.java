package com.lezo.iscript.crawler.dom.browser;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

public class ScriptLocation {
	private URL url;
	private String target;

	public ScriptLocation() {
	}

	public ScriptLocation(String url) {
		setHref(url);
	}

	public URL getURL() {
		return url;
	}

	public String getHash() {
		URL url = this.getURL();
		return url == null ? null : url.getRef();
	}

	public String getHost() {
		URL url = this.getURL();
		if (url == null) {
			return null;
		}
		return url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort());
	}

	public String getHostname() {
		URL url = this.getURL();
		if (url == null) {
			return null;
		}
		return url.getHost();
	}

	public String getPathname() {
		URL url = this.getURL();
		return url == null ? null : url.getPath();
	}

	public String getPort() {
		URL url = this.getURL();
		if (url == null) {
			return null;
		}
		int port = url.getPort();
		return port == -1 ? null : String.valueOf(port);
	}

	public String getProtocol() {
		URL url = this.getURL();
		if (url == null) {
			return null;
		}
		return url.getProtocol() + ":";
	}

	public String getSearch() {
		URL url = this.getURL();
		String query = url == null ? null : url.getQuery();
		// Javascript requires "?" in its search string.
		return query == null ? "" : "?" + query;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String value) {
		this.target = value;
	}

	public String getHref() {
		return url.toString();
	}

	public void setHref(String href) {
		try {
			url = StringUtils.isBlank(href) ? null : new URL(href);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
	}

	public void replace(String href) {
		this.setHref(href);
	}

	public String toString() {
		// This needs to be href. Callers
		// rely on that.
		return this.getHref();
	}
}
