package com.lezo.iscript.yeam.mina;

import java.util.Timer;
import java.util.TimerTask;

import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.common.storage.StorageTimeTrigger;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.storage.ResultFutureStorager;

public class ClientMain {

	public static void main(String[] args) {
		new ClientMain().start();
	}

	public void start() {
		IoClient ioClient = new IoClient();
		SessionSender.getInstance().setIoClient(ioClient);

		final StorageTimeTrigger timeTrigger = new StorageTimeTrigger();
		StorageListener<?> listener = ResultFutureStorager.getInstance();
		timeTrigger.addListener(listener.getClass(), listener);
		long delay = 1000L;
		long period = 30000L;
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				timeTrigger.doTrigger();
			}
		}, delay, period);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				PersistentCollector.getInstance().getBufferWriter().flush();
			}
		}, 60000L, 1000L);
	}
}
