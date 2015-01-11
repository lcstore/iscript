package com.lezo.iscript.yeam.mina;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.common.storage.StorageTimeTrigger;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.storage.ResultFutureStorager;

public class ClientMain {
	private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClientMain.class);

	// public static void main(String[] args) {
	// new ClientMain().start();
	// }

	public void start() {
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
	}
}
