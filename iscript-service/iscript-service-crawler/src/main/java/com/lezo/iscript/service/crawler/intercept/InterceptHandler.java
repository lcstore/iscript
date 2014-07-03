package com.lezo.iscript.service.crawler.intercept;

import java.lang.reflect.Method;

public interface InterceptHandler {
	Object doIntercepter(Object proxy, Method method, Object[] args);

	boolean isIntercept(Method method);
}
