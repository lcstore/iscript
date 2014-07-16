package com.lezo.iscript.yeam.simple.event;

import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.simple.event.woker.ConfigResponeWorker;
import com.lezo.iscript.yeam.simple.event.woker.TaskResponeWorker;

public class ResponeWorkerFactory {
	private static final Runnable NONE_WORKER = new NoneWorker();

	public Runnable createWorker(IoRespone ioRespone) {
		Runnable worker = NONE_WORKER;
		switch (ioRespone.getType()) {
		case IoConstant.EVENT_TYPE_CONFIG: {
			worker = new ConfigResponeWorker(ioRespone);
			break;
		}
		case IoConstant.EVENT_TYPE_TASK: {
			worker = new TaskResponeWorker(ioRespone);
			break;
		}
		case IoConstant.EVENT_TYPE_NONE: {
			break;
		}
		default:
			break;
		}
		return worker;
	}

	static class NoneWorker implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}

	}

}
