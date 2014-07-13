package com.lezo.iscript.yeam.simple.event.woker;

import com.lezo.iscript.yeam.io.IoRespone;

public class TaskResponeWorker implements Runnable {
	private IoRespone ioRespone;
	private static final Object lock = new Object();

	public TaskResponeWorker(IoRespone ioRespone) {
		super();
		this.ioRespone = ioRespone;
	}

	@Override
	public void run() {
		synchronized (lock) {
			System.out.println("");
		}
	}
}
