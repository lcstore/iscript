package com.lezo.iscript.yeam.client.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

import com.lezo.iscript.spring.remote.ProxyFactoryBeanUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.service.ResulterService;
import com.lezo.iscript.yeam.service.TaskerService;


public class ClientRemoteUtils {
	public static TaskerService getTaskerService() throws IOException {
		String taskerHost = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_TASKER_HOST);
		return getTaskerService(taskerHost);
	}

	public static TaskerService getTaskerService(String taskerHost) throws IOException {
		String serviceUrl = taskerHost;
		if (StringUtils.isEmpty(serviceUrl)) {
			throw new IOException("Can not inovker bean by empty url.");
		}
		String servlet = "taskservlet/service";
		if (!serviceUrl.endsWith(servlet)) {
			if (!serviceUrl.endsWith("/")) {
				serviceUrl += "/";
			}
			serviceUrl += servlet;
		}
		TaskerService taskerService = null;
		HttpInvokerRequestExecutor excutor = null;
		taskerService = (TaskerService) ProxyFactoryBeanUtils.createHttpInvokerProxyFactoryBean(serviceUrl,
				TaskerService.class, excutor);
		return taskerService;
	}

	public static ResulterService getResulterService(String resulterHost) throws IOException {
		String serviceUrl = resulterHost;
		if (StringUtils.isEmpty(serviceUrl)) {
			throw new IOException("Can not inovker bean by empty url.");
		}
		String servlet = "resultservlet/service";
		if (!serviceUrl.endsWith(servlet)) {
			if (!serviceUrl.endsWith("/")) {
				serviceUrl += "/";
			}
			serviceUrl += servlet;
		}
		ResulterService resulterService = null;
		HttpInvokerRequestExecutor excutor = null;
		resulterService = (ResulterService) ProxyFactoryBeanUtils.createHttpInvokerProxyFactoryBean(serviceUrl,
				ResulterService.class, excutor);
		return resulterService;
	}
}
