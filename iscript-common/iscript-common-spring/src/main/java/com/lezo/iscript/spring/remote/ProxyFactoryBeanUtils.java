package com.lezo.iscript.spring.remote;

import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

public class ProxyFactoryBeanUtils {

	public static Object createHttpInvokerProxyFactoryBean(String serviceUrl, Class<?> serviceInterface,
			HttpInvokerRequestExecutor excutor) {
		HttpInvokerProxyFactoryBean proxyBean = new HttpInvokerProxyFactoryBean();
		proxyBean.setServiceInterface(serviceInterface);
		proxyBean.setServiceUrl(serviceUrl);
		if (excutor != null) {
			proxyBean.setHttpInvokerRequestExecutor(excutor);
		}
		proxyBean.afterPropertiesSet();
		return proxyBean.getObject();
	}

	public static Object createHessianProxyFactoryBean(String serviceUrl, Class<?> serviceInterface, String username,
			String password, long timeout, boolean chunkedPost) {
		HessianProxyFactoryBean proxyBean = new HessianProxyFactoryBean();
		proxyBean.setServiceInterface(serviceInterface);
		proxyBean.setServiceUrl(serviceUrl);
		proxyBean.setChunkedPost(chunkedPost);
		proxyBean.setReadTimeout(timeout);
		proxyBean.setUsername(username);
		proxyBean.setPassword(password);
		proxyBean.afterPropertiesSet();
		return proxyBean.getObject();
	}
}
