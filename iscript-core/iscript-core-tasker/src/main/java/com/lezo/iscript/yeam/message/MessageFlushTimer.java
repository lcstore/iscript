package com.lezo.iscript.yeam.message;

public class MessageFlushTimer {
	public void run() {
		MessageCacher.getInstance().getBufferWriter().flush();
	}
}
