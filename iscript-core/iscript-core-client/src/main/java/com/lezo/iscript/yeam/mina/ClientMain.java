package com.lezo.iscript.yeam.mina;

import java.util.Timer;
import java.util.TimerTask;

import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.common.storage.StorageTimeTrigger;
import com.lezo.iscript.utils.PropertiesUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.storage.ResultFutureStorager;

public class ClientMain {
	private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClientMain.class);

	public static void main(String[] args) {
		new ClientMain().start();
	}

	public void start() {
		PropertiesUtils.loadQuietly(PropertiesUtils.class.getClassLoader().getResourceAsStream("config/client.properties"));

		IoClient ioClient = new IoClient();
		SessionSender.getInstance().setIoClient(ioClient);

		final StorageTimeTrigger timeTrigger = new StorageTimeTrigger();
		StorageListener<?> listener = ResultFutureStorager.getInstance();
		timeTrigger.addListener(listener.getClass(), listener);
		long delay = 30000L;
		long period = 1000L;
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					timeTrigger.doTrigger();
				} catch (Exception e) {
					logger.warn("trigger storage,cause:", e);
				}
			}
		}, delay, period);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					PersistentCollector.getInstance().getBufferWriter().flush();
				} catch (Exception e) {
					logger.warn("flush persistent,cause:", e);
				}
			}
		}, 60000L, 1000L);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					IoRequest ioRequest = new IoRequest();
					ioRequest.setType(IoRequest.REQUEST_REPORT);
					ioRequest.setHeader(HeaderUtils.getHeader().toString());
					SessionSender.getInstance().send(ioRequest);
					logger.info("client heartbeat.head:" + ioRequest.getHeader());
				} catch (Exception e) {
					logger.warn("client report,cause:", e);
				}
			}
		}, 1000L, 60000L);
	}
}
