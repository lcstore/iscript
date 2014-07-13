package com.lezo.iscript.yeam.simple.event;

import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.simple.event.woker.ConfigResponeWorker;
import com.lezo.iscript.yeam.simple.event.woker.TaskResponeWorker;

public class ResponeWorkerFactory {

	public Runnable createWorker(IoRespone ioRespone) {
		Runnable worker = null;
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

}
