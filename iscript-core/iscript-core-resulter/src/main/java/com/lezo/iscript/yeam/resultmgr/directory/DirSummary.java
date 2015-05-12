package com.lezo.iscript.yeam.resultmgr.directory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DirSummary {
	private Lock locker = new ReentrantLock();
	private DirMeta dirBean;
	private int count;
	private long fromStamp;
	private long toStamp;
	private boolean isDone = false;
	private Map<String, String> paramMap;

	public DirSummary() {
		paramMap = new HashMap<String, String>();
		paramMap.put("marker", "");
	}

	public DirMeta getDirBean() {
		return dirBean;
	}

	public void setDirBean(DirMeta dirBean) {
		this.dirBean = dirBean;
	}

	public long getFromStamp() {
		return fromStamp;
	}

	public synchronized void setFromStamp(long fromStamp) {
		this.fromStamp = fromStamp;
	}

	public long getToStamp() {
		return toStamp;
	}

	public synchronized void setToStamp(long toStamp) {
		this.toStamp = toStamp;
	}

	public int getCount() {
		return count;
	}

	public synchronized void setCount(int count) {
		this.count = count;
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public Lock getLocker() {
		return locker;
	}

	public boolean isDone() {
		return isDone;
	}

	public synchronized void setDone(boolean isDone) {
		this.isDone = isDone;
	}
}
