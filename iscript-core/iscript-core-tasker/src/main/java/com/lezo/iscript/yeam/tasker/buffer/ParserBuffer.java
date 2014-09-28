package com.lezo.iscript.yeam.tasker.buffer;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.common.loader.ClassUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.ConfigWritable;
import com.lezo.iscript.yeam.writable.ParserWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ParserBuffer {
	private static Logger log = Logger.getLogger(ParserBuffer.class);
	private ConcurrentHashMap<String, ParserWritable> configMap = new ConcurrentHashMap<String, ParserWritable>();
	private volatile long maxStamp = 0;

	private ParserBuffer() {
	}

	private static final class InstanceHolder {
		private static final ParserBuffer INSTANCE = new ParserBuffer();
	}

	public static ParserBuffer getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public synchronized boolean addConfig(String name, String content, long stamp) {
		try {
			ConfigParser parser = newClassParser(content);
			ParserWritable ParserWritable = new ParserWritable();
			ParserWritable.setConfigParser(parser);
			ParserWritable.setStamp(stamp);
			configMap.put(parser.getName(), ParserWritable);
			maxStamp = stamp > maxStamp ? stamp : maxStamp;
			return true;
		} catch (Exception e) {
			String message = String.format("fail to buffer config[%s].", name);
			log.warn(message, e);
		}
		return false;
	}

	public synchronized boolean addConfig(ParserWritable parserWritable) {
		try {
			configMap.put(parserWritable.getConfigParser().getName(), parserWritable);
			maxStamp = parserWritable.getStamp() > maxStamp ? parserWritable.getStamp() : maxStamp;
			return true;
		} catch (Exception e) {
			String message = String.format("fail to buffer config[%s].", parserWritable.getConfigParser().getName());
			log.warn(message, e);
		}
		return false;
	}

	private ConfigParser newClassParser(String codeSource) throws Exception {
		ConfigParser parser = (ConfigParser) ClassUtils.newObject(codeSource);
		return parser;
	}

	public ParserWritable getParser(String name) {
		return configMap.get(name);
	}

	public long getStamp() {
		return maxStamp;
	}

	public Iterator<Entry<String, ParserWritable>> unmodifyIterator() {
		Collection<Entry<String, ParserWritable>> unmodifyList = CollectionUtils.unmodifiableCollection(configMap
				.entrySet());
		return unmodifyList.iterator();
	}

}
