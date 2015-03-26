package com.lezo.iscript.yeam.mina.utils;

import java.util.concurrent.atomic.AtomicLong;

public class ServerTimeUtils {

	private static AtomicLong offsetMills = new AtomicLong(0);

	public static void setTargetMills(Long targetMills, Long delayMills) {
		if (targetMills == null || targetMills < 1) {
			return;
		}
		delayMills = delayMills == null ? 0 : delayMills;
		long offset = System.currentTimeMillis() - targetMills - delayMills;
		offsetMills.set(offset);
	}

	public static long getTimeMills() {
		return System.currentTimeMillis() - offsetMills.get();
	}
}
