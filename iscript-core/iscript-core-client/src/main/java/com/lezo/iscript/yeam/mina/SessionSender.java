package com.lezo.iscript.yeam.mina;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;

import com.lezo.iscript.yeam.io.IoRequest;

public class SessionSender {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SessionSender.class);
	private static SessionSender instance;
	private IoClient ioClient;

	private SessionSender() {
	}

	public static SessionSender getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (SessionSender.class) {
			if (instance == null) {
				instance = new SessionSender();
			}
		}
		return instance;
	}

	public void send(IoRequest request) {
		WriteFuture wfuture = ioClient.getSession().write(request);
		// wfuture.awaitUninterruptibly();
		wfuture.addListener(new IoFutureListener<IoFuture>() {
			@Override
			public void operationComplete(IoFuture future) {
				if (!future.isDone()) {
					logger.warn("not done.msg:" + future);
				}
			}
		});
	}

	public boolean send(IoRequest request, long timeoutMillis) {
		WriteFuture wfuture = ioClient.getSession().write(request);
		return wfuture.awaitUninterruptibly(timeoutMillis);
	}

	public IoSession getSession() {
		return ioClient.getSession();
	}

	public IoClient getIoClient() {
		return ioClient;
	}

	public void setIoClient(IoClient ioClient) {
		this.ioClient = ioClient;
	}

}
