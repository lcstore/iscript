package com.lezo.iscript;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IoWatch {
	private IoSeed ioSeed;
	private int totalCount;
	private int fetchCount;
	private int errorCount;
	private boolean done;
	private final long fromMills = System.currentTimeMillis();
	private AtomicLong toMills = new AtomicLong();

	public long getToMills() {
		return toMills.get();
	}

	public void setToMills(long toMills) {
		if (getToMills() < toMills) {
			this.toMills.set(toMills);
		}
	}
}
