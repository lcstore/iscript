package com.lezo.iscript.yeam.resultmgr;

import java.util.Map;

import com.qiniu.api.auth.digest.Mac;

public class QiniuMacFactory {
	private Map<String, Mac> macMap;

	public Mac getMac(String host) {
		return macMap.get(host);
	}

	public Map<String, Mac> getMacMap() {
		return macMap;
	}

	public void setMacMap(Map<String, Mac> macMap) {
		this.macMap = macMap;
	}
}
