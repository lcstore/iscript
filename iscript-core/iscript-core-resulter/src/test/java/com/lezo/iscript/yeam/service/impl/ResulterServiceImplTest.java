package com.lezo.iscript.yeam.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

import com.lezo.iscript.spring.remote.ProxyFactoryBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.service.ResulterService;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResulterServiceImplTest {

	@Test
	public void test() {
		String[] locations = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(locations);
		String serviceUrl = "http://localhost:8088/taskservlet/service";
		ResulterService resulterService = null;
		HttpInvokerRequestExecutor excutor = null;
		resulterService = (ResulterService) ProxyFactoryBeanUtils.createHttpInvokerProxyFactoryBean(serviceUrl,
				ResulterService.class, excutor);
		List<ResultWritable> resultList = new ArrayList<ResultWritable>();
		ResultWritable rWritable = new ResultWritable();
		JSONObject rsObject = new JSONObject();
		JSONObject rs = new JSONObject();
		JSONObject rsValue = new JSONObject();
		JSONUtils.put(rsValue, "x", 10);
		JSONUtils.put(rsValue, "y", 100);
		JSONUtils.put(rsValue, "sum", 110);
		JSONUtils.put(rs, "rs", rsValue);
		JSONObject argsObject = new JSONObject();
		JSONUtils.put(argsObject, "x", 10);
		JSONUtils.put(argsObject, "y", 100);
		JSONUtils.put(argsObject, "type", "sum");
		JSONUtils.put(rs, "args", argsObject);
		JSONUtils.put(rsObject, "rs", rs.toString());
		String result = rsObject.toString();
		rWritable.setResult(result);
		resultList.add(rWritable);
		resulterService.doSubmit(resultList);
	}
}
