package com.lezo.iscript.yeam.client;

public abstract class AbstractWorker implements Runnable {

	@Override
	public final void run() {
		int executeCount = 0;
		while (true) {
			Exception ex = null;
			try {
				doExecute();
			} catch (Exception e) {
				ex = e;
			}
			if (!doNext(executeCount, ex)) {
				break;
			}
		}
	}

	public abstract void doExecute() throws Exception;

	public boolean doNext(int executeCount, Exception ex) {
		if (ex instanceof InterruptedException || ex instanceof ClassNotFoundException) {
			return false;
		}
		return true;
	}

}
