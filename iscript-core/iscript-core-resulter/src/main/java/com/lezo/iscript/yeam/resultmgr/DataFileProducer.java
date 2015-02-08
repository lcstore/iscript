package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.resultmgr.directory.DirectoryDescriptor;
import com.lezo.iscript.yeam.resultmgr.directory.DirectoryLockUtils;
import com.lezo.iscript.yeam.resultmgr.directory.DirectoryTracker;
import com.lezo.rest.QiniuBucketMac;
import com.lezo.rest.QiniuBucketMacFactory;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rsf.ListItem;
import com.qiniu.api.rsf.ListPrefixRet;
import com.qiniu.api.rsf.RSFClient;
import com.qiniu.api.rsf.RSFEofException;

public class DataFileProducer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(DataFileProducer.class);
	private static final String DIR_SEPARATOR = "/";
	private ThreadPoolExecutor executor = (ThreadPoolExecutor) SpringBeanUtils.getBean("fileConsumeExecutor");
	private DirectoryTracker tracker;

	public DataFileProducer(DirectoryTracker tracker) {
		this.tracker = tracker;
	}

	@Override
	public void run() {
		DirectoryDescriptor descriptor = this.tracker.getDescriptor();
		QiniuBucketMac bucketMac = QiniuBucketMacFactory.getBucketMac(descriptor.getBucketName());
		Mac mac = bucketMac == null ? null : bucketMac.getMac();
		if (mac == null) {
			throw new IllegalArgumentException("can not get QiniuMac:" + descriptor.getBucketName() + "." + descriptor.getDomain());
		}
		Lock locker = DirectoryLockUtils.findLock(descriptor.getDirectoryKey());
		if (locker == null) {
			throw new IllegalArgumentException("can not get locker:" + descriptor.getDirectoryKey());
		}
		if (locker.tryLock()) {
			try {
				locker.lock();
				doWork(descriptor, mac);
			} catch (Exception e) {
				logger.warn("do file produce.cause:", e);
			} finally {
				locker.unlock();
			}
		} else {
			logger.warn(descriptor.getDirectoryKey() + " is running...");
		}
	}

	private void doWork(DirectoryDescriptor descriptor, Mac mac) {
		RSFClient client = new RSFClient(mac);
		ListPrefixRet ret = null;
		int limit = 100;
		int retry = 0;
		int count = 0;
		while (true) {
			ret = client.listPrifix(descriptor.getBucketName(), descriptor.getDataPath(), this.tracker.getMarker(), limit);
			if (ret.statusCode >= 200 && ret.statusCode < 300) {
				List<ListItem> acceptList = getAccepts(ret.results, this.tracker.getStamp());
				this.tracker.setMarker(ret.marker);
				if (!CollectionUtils.isEmpty(acceptList)) {
					count += acceptList.size();
					logger.info("directoryKey:" + this.tracker.getDescriptor().getDirectoryKey() + ", :" + this.tracker.getStamp() + ",totalCount:" + count + ",newCount:" + acceptList.size());
					createDataFileConsumer(descriptor, mac, acceptList);
					this.tracker.setFileCount(this.tracker.getFileCount() + acceptList.size());
					this.tracker.setStamp(getMaxStamp(acceptList));
				}
				if (ret.results.size() < limit) {
					break;
				}
				if (ret.exception instanceof RSFEofException) {
					// error handler
					break;
				}
				retry = 0;
			} else {
				logger.warn(ret.response + ",retry:" + (++retry), ret.exception);
				if (retry > 3) {
					break;
				}
			}

		}
		logger.info("directoryKey:" + this.tracker.getDescriptor().getDirectoryKey() + ", :" + this.tracker.getStamp() + ",totalCount:" + this.tracker.getFileCount() + ",newCount:" + count);
	}

	private void createDataFileConsumer(DirectoryDescriptor descriptor, Mac mac, List<ListItem> itemList) {
		String type = getTypeFromPath(descriptor.getDataPath());
		for (ListItem item : itemList) {
			try {
				DataFileWrapper dataFileWrapper = new DataFileWrapper();
				dataFileWrapper.setBucketName(descriptor.getBucketName());
				dataFileWrapper.setDomain(descriptor.getDomain());
				dataFileWrapper.setItem(item);
				dataFileWrapper.setMac(mac);
				executor.execute(new DataFileConsumer(type, dataFileWrapper));
			} catch (Exception e) {
				logger.warn("File:" + item.key + ",cause:", e);
			}
		}
	}

	private long getMaxStamp(List<ListItem> itemList) {
		long max = 0L;
		for (ListItem item : itemList) {
			if (max < item.putTime) {
				max = item.putTime;
			}
		}
		return max;
	}

	private List<ListItem> getAccepts(List<ListItem> results, long stamp) {
		if (CollectionUtils.isEmpty(results)) {
			return Collections.emptyList();
		}
		List<ListItem> itemList = new ArrayList<ListItem>(results.size());
		for (ListItem rs : results) {
			if (stamp > rs.putTime) {
				continue;
			}
			itemList.add(rs);
		}
		return itemList;
	}

	private String getTypeFromPath(String dataPath) {
		int fromIndex = dataPath.indexOf(DIR_SEPARATOR) + 1;
		fromIndex = dataPath.indexOf(DIR_SEPARATOR, fromIndex) + 1;
		int toIndex = dataPath.indexOf(DIR_SEPARATOR, fromIndex);
		return dataPath.substring(fromIndex, toIndex);
	}
}
