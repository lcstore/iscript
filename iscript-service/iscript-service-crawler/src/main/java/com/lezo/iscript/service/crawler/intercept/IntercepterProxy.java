package com.lezo.iscript.service.crawler.intercept;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

public class IntercepterProxy<T> implements InvocationHandler, FactoryBean<T> {
	private Object targetObject;
	private Class<T> mapperInterface;
	private InterceptHandler interceptHandler;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (interceptHandler.isIntercept(method)) {
			interceptHandler.doIntercepter(proxy, method, args);
		}
		return method.invoke(this.targetObject, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, this);
	}

	@Override
	public Class<?> getObjectType() {
		return mapperInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
	}

	public void setMapperInterface(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	public void setInterceptHandler(InterceptHandler interceptHandler) {
		this.interceptHandler = interceptHandler;
	}
}
