package com.lezo.iscript.service.crawler.intercept;

import java.lang.reflect.Method;

public interface InterceptHandler {
	Object doIntercepter(Object proxy, Method method, Object[] args) throws Exception;

	boolean isIntercept(Method method);
}
