package com.lezo.iscript.yeam.defend.update;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.defend.client.ClientExecuter;

public class ClientExecuterTest {

	public static void main(String[] args){
		ObjectBuilder.newObject(ClientConstant.CLIENT_PATH,"C:/yeam");
		ObjectBuilder.newObject(ClientConstant.CLIENT_NAME,"lezo2");
		ObjectBuilder.newObject(ClientConstant.CLIENT_TASKER_HOST,"http://localhost:8088/tasker/taskservlet/service");
		ClientExecuter.getClientExecuter().doExecute();
	}
}
