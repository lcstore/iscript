package com.lezo.iscript.updater.service.impl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.updater.http.HttpUtils;
import com.lezo.iscript.updater.service.IUpdateManager;
import com.lezo.iscript.updater.utils.NameUtils;
import com.lezo.iscript.updater.utils.PropertiesUtils;

public class UpdateManager implements IUpdateManager {
	private static Logger logger = LoggerFactory.getLogger(UpdateManager.class);
	private static final String KEY_SERVER_BASE_HOST = "server_base_host";
	private static final String VALUE_BASE_PATH = "updater/";
	private DefaultHttpClient client = HttpUtils.getDefaultClient();

	@Override
	public boolean extractTo(File destFile) {
		String versionUrl = getBaseUrlPath() + "entity?name=" + NameUtils.APP_NAME;
		HttpGet get = new HttpGet(versionUrl);
		try {
			HttpResponse respone = client.execute(get);
			if (respone.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				FileUtils.writeByteArrayToFile(destFile, EntityUtils.toByteArray(respone.getEntity()));
				return destFile.exists();
			} else {
				EntityUtils.consumeQuietly(respone.getEntity());
			}
		} catch (Exception e) {
			logger.warn("write to file:" + destFile + ",cause:", e);
		} finally {
			if (!get.isAborted()) {
				get.abort();
			}
		}
		return false;
	}

	@Override
	public String getCurrentVersion() {
		String versionUrl = getBaseUrlPath() + "version?name=" + NameUtils.APP_NAME;
		HttpGet get = new HttpGet(versionUrl);
		try {
			HttpResponse respone = client.execute(get);
			if (respone.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String version = EntityUtils.toString(respone.getEntity(), "UTF-8");
				return version.trim();
			} else {
				EntityUtils.consumeQuietly(respone.getEntity());
			}
		} catch (Exception e) {
			logger.warn("fetch verion,cause:", e);
		} finally {
			if (!get.isAborted()) {
				get.abort();
			}
		}
		return null;
	}

	private String getBaseUrlPath() {
		String baseHost = PropertiesUtils.getProperty(KEY_SERVER_BASE_HOST);
		if (StringUtils.isBlank(baseHost)) {
			throw new IllegalArgumentException("empty value.check properties for key:" + KEY_SERVER_BASE_HOST);
		}
		baseHost = baseHost.trim();
		if (!baseHost.endsWith("/")) {
			baseHost += "/";
		}
		return baseHost + VALUE_BASE_PATH;
	}

}
