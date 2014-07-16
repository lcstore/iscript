package com.lezo.iscript.yeam.simple;

import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.simple.utils.HeaderUtils;

public class IoClientTest {
	public static void main(String[] args) {
		// String host = "localhost";
		// int port = 1111;
		// IoClient ioClient = new IoClient(host, port);
		// IoSession session = ioClient.getSession();
		// session.write("update config");

		IoRequest request = new IoRequest();
		request.setHeader(HeaderUtils.getHeader().toString());
		SessionSender.getInstance().send(request);
		
	}

}
