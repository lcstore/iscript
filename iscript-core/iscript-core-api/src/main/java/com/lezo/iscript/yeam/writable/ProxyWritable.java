package com.lezo.iscript.yeam.writable;

import java.io.Serializable;

public class ProxyWritable implements Serializable {
	private static final long serialVersionUID = -6677591600490429782L;
	private Long id;
	private Long ip;
	private int port;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIp() {
		return ip;
	}

	public void setIp(Long ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
