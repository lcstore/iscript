package com.lezo.iscript.yeam.resultmgr.directory;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.lezo.iscript.yeam.resultmgr.ExecutorUtils;

public class DirSummaryCacher {
	private static final Object LOCKER = new Object();
	private ConcurrentHashMap<String, DirSummary> summaryMap = new ConcurrentHashMap<String, DirSummary>();
	private static DirSummaryCacher instance = new DirSummaryCacher();

	private DirSummaryCacher() {

	}

	public static DirSummaryCacher getInstance() {
		return instance;
	}

	public DirSummary getDirStream(String dirKey) {
		return summaryMap.get(dirKey);
	}

	public void addDirStream(String dirKey, DirSummary dirStream) {
		summaryMap.put(dirKey, dirStream);
	}

	public DirSummary remove(String dirKey) {
		if (dirKey == null) {
			return null;
		}
		return summaryMap.remove(dirKey);
	}

	public Iterator<Entry<String, DirSummary>> iterator() {
		return summaryMap.entrySet().iterator();
	}

	public void fireEvent(DirMeta dirBean) {
		String key = dirBean.toDirKey();
		DirSummary hasStream = summaryMap.get(key);
		if (hasStream == null) {
			synchronized (LOCKER) {
				hasStream = summaryMap.get(key);
				if (hasStream == null) {
					hasStream = new DirSummary();
					hasStream.setDirBean(dirBean);

					Calendar c = Calendar.getInstance();
					c.setTime(dirBean.getCreateTime());
					c.add(Calendar.MINUTE, -10);
					hasStream.setFromStamp(c.getTimeInMillis());
					hasStream.setToStamp(hasStream.getFromStamp());
					addDirStream(key, hasStream);
				}
			}
		}
		ExecutorUtils.getFileProduceExecutor().execute(new DirFileScanner(hasStream));
	}

}
