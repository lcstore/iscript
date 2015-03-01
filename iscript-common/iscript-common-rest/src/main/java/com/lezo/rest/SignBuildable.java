package com.lezo.rest;

import java.util.Map;

public interface SignBuildable {
	String getSign(Map<String, Object> inMap) throws Exception;
}
