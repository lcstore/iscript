package com.lezo.iscript.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class BatchMapperProxy implements InvocationHandler {
	private Object targetObject;

	public BatchMapperProxy(Object targetObject) {
		super();
		this.targetObject = targetObject;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("batchUpdate")) {
			System.out.println("batu");
			return null;
		}
		return null;
	}

}
