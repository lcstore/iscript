package com.lezo.iscript.yeam.client.service.impl;

import java.io.IOException;

import org.junit.Test;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.client.utils.ClientRemoteUtils;
import com.lezo.iscript.yeam.service.TaskerService;
import com.lezo.iscript.yeam.writable.ClientWritable;
import com.lezo.iscript.yeam.writable.RemoteWritable;

public class RemoteCallerTest {

	@Test
	public void test() throws IOException {
		String taskerHost = "http://dtasker.sturgeon.mopaas.com/taskservlet/service";
		TaskerService taskerService = ClientRemoteUtils.getTaskerService(taskerHost);
		ClientWritable client = new ClientWritable();
		client.getParam().put(ClientConstant.CLIENT_NAME, "test");
		RemoteWritable<?> taskWrapper = taskerService.getMore(client);
	}
}
