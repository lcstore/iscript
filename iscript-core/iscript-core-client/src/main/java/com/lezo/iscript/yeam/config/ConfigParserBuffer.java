package com.lezo.iscript.yeam.config;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.common.loader.ClassUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class ConfigParserBuffer {
	private static Logger log = Logger.getLogger(ConfigParserBuffer.class);
	private ConcurrentHashMap<String, ConfigParser> configMap = new ConcurrentHashMap<String, ConfigParser>();
	private volatile long stamp = 0;

	private ConfigParserBuffer() {
	}

	private static final class InstanceHolder {
		private static final ConfigParserBuffer INSTANCE = new ConfigParserBuffer();
	}

	public static ConfigParserBuffer getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public synchronized boolean addConfig(String name, ConfigWritable configWritable) {
		if (stamp > configWritable.getStamp()) {
			log.warn("ignore config:" + configWritable.getName() + ",new stamp:" + configWritable.getStamp()
					+ ",but buffer's stamp:" + stamp);
			return false;
		}
		ConfigParser parser = createParser(configWritable);
		if (parser == null) {
			return false;
		}
		configMap.put(configWritable.getName(), parser);
		stamp = configWritable.getStamp();
		return true;
	}

	private ConfigParser createParser(ConfigWritable configWritable) {
		ConfigParser parser = null;
		switch (configWritable.getType()) {
		case ConfigWritable.CONFIG_TYPE_SCRIPT: {
			parser = newScriptParser(configWritable);
			break;
		}
		case ConfigWritable.CONFIG_TYPE_JAVA: {
			parser = newClassParser(configWritable);
			break;
		}
		default:
			break;
		}
		return parser;
	}

	private ConfigParser newScriptParser(ConfigWritable configWritable) {
		try {
			String config = new String(configWritable.getContent(), ClientConstant.CLIENT_CHARSET);
			ConfigParser parser = new ScriptConfigParser(config);
			return parser;
		} catch (UnsupportedEncodingException e) {
			log.error("fail to buffer config[" + configWritable.getName() + "]", e);
		}
		return null;
	}

	private ConfigParser newClassParser(ConfigWritable configWritable) {
		byte[] bytes = configWritable.getContent();
		try {
			String codeSource = new String(bytes, "UTF-8");
			ConfigParser parser = (ConfigParser) ClassUtils.newObject(codeSource);
			return parser;
		} catch (Exception ex) {
			log.error("fail to buffer config[" + configWritable.getName() + "]", ex);
		}
		return null;
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
