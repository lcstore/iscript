package com.lezo.iscript.yeam.writable;

import java.io.Serializable;

import com.lezo.iscript.yeam.service.ConfigParser;

public class ParserWritable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long stamp;
	private ConfigParser configParser;

	public long getStamp() {
		return stamp;
	}

	public void setStamp(long stamp) {
		this.stamp = stamp;
	}

	public ConfigParser getConfigParser() {
		return configParser;
	}

	public void setConfigParser(ConfigParser configParser) {
		this.configParser = configParser;
	}

}
