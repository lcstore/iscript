package com.lezo.iscript.service.crawler.dao.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class BatchUpdateProxy implements InvocationHandler {
	private Object targetObject;

	public BatchUpdateProxy(Object targetObject) {
		this.targetObject = targetObject;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("batchUpdate")) {
			System.out.println("batu");
			return null;
		}
		return method.invoke(targetObject, args);
	}

}
