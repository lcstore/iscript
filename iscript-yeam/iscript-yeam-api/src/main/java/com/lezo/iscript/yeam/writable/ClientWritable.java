package com.lezo.iscript.yeam.writable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ClientWritable implements Serializable {
	private static final long serialVersionUID = -1671788647643497754L;
	private String version;
	private String name;
	private Map<String, Object> param = new HashMap<String, Object>();

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

}
