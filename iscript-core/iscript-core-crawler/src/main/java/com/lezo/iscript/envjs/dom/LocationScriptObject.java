package com.lezo.iscript.envjs.dom;

import java.net.URL;
import java.util.logging.Logger;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Document;

public class LocationScriptObject extends NativeJavaObject {
	private static final long serialVersionUID = -7096284504871231194L;
	private static final Logger logger = Logger.getLogger(LocationScriptObject.class.getName());

	public LocationScriptObject(Scriptable scope, Object object) {
		super(scope, object, object.getClass());
	}

	private URL getURL() {
		URL url;
		try {
			Document document = null;
			url = document == null ? null : new URL(document.getDocumentURI());
		} catch (java.net.MalformedURLException mfu) {
			url = null;
		}
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

	private String target;

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String value) {
		this.target = value;
	}

	public String getHref() {
		Document document = null;
		return document == null ? null : document.getDocumentURI();
	}

	public void setHref(String uri) {
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
