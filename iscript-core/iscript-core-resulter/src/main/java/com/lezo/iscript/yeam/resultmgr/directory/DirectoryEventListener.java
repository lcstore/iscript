package com.lezo.iscript.yeam.resultmgr.directory;

import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.resultmgr.DataFileProducer;

public class DirectoryEventListener {

	private ConcurrentHashMap<String, DirectoryTracker> directoryMap = new ConcurrentHashMap<String, DirectoryTracker>();

	public void fireEvent(DirectoryDescriptor event) {
		DirectoryTracker tracker = directoryMap.get(event.getDirectoryKey());
		if (tracker == null) {
			// set back 10min for stamp
			Calendar c = Calendar.getInstance();
			c.setTime(event.getCreateTime());
			c.add(Calendar.MINUTE, -10);
			tracker = new DirectoryTracker(event);
			tracker.setStamp(c.getTimeInMillis());
			directoryMap.put(event.getDirectoryKey(), tracker);
			DirectoryLockUtils.addLock(event.getDirectoryKey(), new ReentrantLock());
		} else {
//			// 重新下载旧结果
//			Calendar c = Calendar.getInstance();
//			c.setTime(event.getCreateTime());
//			c.add(Calendar.MINUTE, -10);
//			if (tracker.getStamp() > c.getTimeInMillis()) {
//				tracker.setStamp(c.getTimeInMillis());
//			}
		}
		ThreadPoolExecutor executor = (ThreadPoolExecutor) SpringBeanUtils.getBean("fileProduceExecutor");
		executor.execute(new DataFileProducer(tracker));
	}
}
