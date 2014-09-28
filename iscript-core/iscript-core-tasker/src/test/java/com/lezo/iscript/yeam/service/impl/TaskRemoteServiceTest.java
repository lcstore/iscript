package com.lezo.iscript.yeam.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

import com.lezo.iscript.spring.remote.ProxyFactoryBeanUtils;
import com.lezo.iscript.yeam.service.TaskRemoteService;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskRemoteServiceTest {

	@Test
	public void test() {
		String serviceUrl = "http://localhost:8088/taskservlet/taskRemoteService";
		TaskRemoteService taskerService = null;
		HttpInvokerRequestExecutor excutor = null;
		taskerService = (TaskRemoteService) ProxyFactoryBeanUtils.createHttpInvokerProxyFactoryBean(serviceUrl,
				TaskRemoteService.class, excutor);
		List<TaskWritable> taskList = new ArrayList<TaskWritable>();
		TaskWritable task = new TaskWritable();
		task.put("bid", "test.remote.insert");
		task.put("level", 100);
		task.put("src", "remoter");
		task.put("type", "remot-insert");
		task.put("url", "http://www.baidu.com/");
		task.put("ctime", new Date());
		taskList.add(task);
		taskerService.addTasks(taskList);

	}
}
