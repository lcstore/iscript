package com.lezo.iscript.yeam.simple.timer;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.simple.storage.ResultStorager;

public class ClientReportTimer extends TimerTask {
	private Logger logger = LoggerFactory.getLogger(ClientReportTimer.class);
	private static volatile boolean running = false;
	private long delay;
	private long period;
	private Timer timer;

	public ClientReportTimer(long delay, long period) {
		super();
		this.delay = delay;
		this.period = period;
		this.timer = new Timer();
		this.timer.schedule(this, delay, period);
	}

	@Override
	public void run() {
		try {
			sendReport();
		} catch (Exception e) {
			logger.warn(ExceptionUtils.getStackTrace(e));
		}
	}

	private void sendReport() {
		ResultStorager.getInstance().doStorage();
	}

}
