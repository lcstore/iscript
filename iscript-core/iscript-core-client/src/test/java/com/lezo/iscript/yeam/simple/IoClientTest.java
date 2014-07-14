package com.lezo.iscript.yeam.simple;

import com.lezo.iscript.yeam.io.IoRequest;

public class IoClientTest {
	public static void main(String[] args) {
		// String host = "localhost";
		// int port = 1111;
		// IoClient ioClient = new IoClient(host, port);
		// IoSession session = ioClient.getSession();
		// session.write("update config");

		IoRequest request = new IoRequest();
		SessionSender.getInstance().send(request);
	}

}
