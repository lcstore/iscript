package com.lezo.iscript.yeam.defend.client;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.defend.DefendClient;
import com.lezo.iscript.yeam.defend.update.handle.ClientPuller;

public class ClientUpdater {
	private static Logger log = Logger.getLogger(ClientUpdater.class);
	private AtomicBoolean runing = new AtomicBoolean(false);
	private AtomicBoolean isClose = new AtomicBoolean(false);

	private ClientUpdater() {
		super();
	}

	private static final class InstanceHolder {
		private static final ClientUpdater INSTANCE = new ClientUpdater();
	}

	public static ClientUpdater getClientUpdater() {
		return InstanceHolder.INSTANCE;
	}

	public void doExecute(final DefendClient client) {
		Timer uTimer = new Timer("Update.Timer");
		long delay = 0;
		long period = 30 * 1000;
		final ClientPuller puller = new ClientPuller();
		uTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isClose.get() || runing.get()) {
					return;
				}
				if (client.getStatus() == DefendClient.CLIENT_RESTART) {
					close();
					return;
				}
				try {
					runing.set(true);
					puller.handleClient(client);
				} catch (Exception ex) {
					log.warn("", ex);
				} finally {
					runing.set(false);
				}

			}
		}, delay, period);
	}

	public boolean isClose() {
		return isClose.get();
	}

	public void close() {
		this.isClose.set(true);
	}
}
