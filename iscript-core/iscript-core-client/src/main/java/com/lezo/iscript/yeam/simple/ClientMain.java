package com.lezo.iscript.yeam.simple;

import java.util.Timer;
import java.util.TimerTask;

import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.common.storage.StorageTimeTrigger;
import com.lezo.iscript.yeam.simple.storage.ResultFutureStorager;

public class ClientMain {
	private Timer timer = new Timer();

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
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				timeTrigger.doTrigger();
			}
		}, delay, period);
	}
}
