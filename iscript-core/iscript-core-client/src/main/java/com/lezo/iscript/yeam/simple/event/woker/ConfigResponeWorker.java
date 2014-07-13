package com.lezo.iscript.yeam.simple.event.woker;

import com.lezo.iscript.yeam.io.IoRespone;

public class ConfigResponeWorker implements Runnable {
	private static final Object lock = new Object();
	private IoRespone ioRespone;

	public ConfigResponeWorker(IoRespone ioRespone) {
		super();
		this.ioRespone = ioRespone;
	}

	@Override
	public void run() {
		// keep ConfigResponeWorker working in the lineS
		synchronized (lock) {
			System.out.println("update config...");
		}
	}
}
