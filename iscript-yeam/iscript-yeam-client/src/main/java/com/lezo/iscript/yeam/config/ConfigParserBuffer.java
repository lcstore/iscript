package com.lezo.iscript.yeam.config;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.loader.ClassReloader;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class ConfigParserBuffer {
	private static Logger log = Logger.getLogger(ConfigParserBuffer.class);
	private ConcurrentHashMap<String, ConfigParser> configMap = new ConcurrentHashMap<String, ConfigParser>();
	private long stamp = 0;

	private ConfigParserBuffer() {
	}

	private static final class InstanceHolder {
		private static final ConfigParserBuffer INSTANCE = new ConfigParserBuffer();
	}

	public static ConfigParserBuffer getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void addConfig(String name, ConfigWritable configWritable) {
		switch (configWritable.getType()) {
		case ConfigWritable.CONFIG_TYPE_SCRIPT: {
			addScript(configWritable);
			break;
		}
		case ConfigWritable.CONFIG_TYPE_JAVA: {
			addClass(configWritable);
			break;
		}
		default:
			break;
		}
		stamp = configWritable.getStamp();
	}

	private void addScript(ConfigWritable configWritable) {
		try {
			String config = new String(configWritable.getContent(), ClientConstant.CLIENT_CHARSET);
			ConfigParser parser = new ScriptConfigParser(config);
			configMap.put(configWritable.getName(), parser);
		} catch (UnsupportedEncodingException e) {
			log.error("fail to buffer config[" + configWritable.getName() + "]", e);
		}
	}

	private void addClass(ConfigWritable configWritable) {
		byte[] bytes = configWritable.getContent();
		ClassReloader reloader = new ClassReloader();
		try {
			Class<?> newClass = reloader.loadClass(configWritable.getName(), bytes);
			ConfigParser parser = (ConfigParser) newClass.newInstance();
			String name = parser.getName();
			if (name == null) {
				name = configWritable.getName();
			}
			configMap.put(name, parser);
		} catch (Exception ex) {
			log.error("fail to buffer config[" + configWritable.getName() + "]", ex);
		}
	}

	public ConfigParser getParser(String name) {
		return configMap.get(name);
	}

	public long getStamp() {
		return stamp;
	}

	public Iterator<Entry<String, ConfigParser>> unmodifyIterator() {
		Collection<Entry<String, ConfigParser>> unmodifyList = CollectionUtils.unmodifiableCollection(configMap
				.entrySet());
		return unmodifyList.iterator();
	}
}
