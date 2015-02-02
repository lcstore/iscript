package com.lezo.iscript.yeam.resultmgr.writer;

public class WriteFlushTimer {
	public void run() {
		WriteNotifyer.getInstance().flush();
	}
}
