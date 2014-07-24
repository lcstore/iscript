package com.lezo.iscript.yeam.simple;

import java.util.Timer;
import java.util.TimerTask;

import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.simple.utils.HeaderUtils;

public class IoClientTest {
	public static void main(String[] args) {
		// String host = "localhost";
		// int port = 1111;
		// IoClient ioClient = new IoClient(host, port);
		// IoSession session = ioClient.getSession();
		// session.write("update config");

		final IoRequest request = new IoRequest();
		request.setHeader(HeaderUtils.getHeader().toString());
		IoClient ioClient = new IoClient();
		SessionSender.getInstance().setIoClient(ioClient);

		long delay = 1000L;
		long period = 30000L;
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				SessionSender.getInstance().send(request);
			}
		}, delay, period);
	}

}
