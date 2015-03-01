package com.lezo.iscript.yeam.defend.client;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;

public class ClientControllerTest {

	public static void main(String[] args) throws Exception {
		String path = "F:/yeam";
		ObjectBuilder.newObject(ClientConstant.CLIENT_PATH, path);
//		ObjectBuilder.newObject(ClientConstant.CLIENT_NAME, "test");
//		ObjectBuilder.newObject(ClientConstant.CLIENT_TASKER_HOST, "http://localhost:8088/tasker/taskservlet/service");
		ClientController ctrl = new ClientController(new ClientLocker(path + File.separator
				+ ClientConstant.CLIENT_WORK_SPACE));
		System.out.println(ctrl.start());
		TimeUnit.MILLISECONDS.sleep(2 * 60 * 1000);
		ctrl.close();
		System.out.println("end ...");
	}
}
