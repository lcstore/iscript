package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.resultmgr.directory.DirectoryDescriptor;
import com.lezo.iscript.yeam.resultmgr.directory.DirectoryLockUtils;
import com.lezo.iscript.yeam.resultmgr.directory.DirectoryTracker;
import com.lezo.rest.data.BaiduPcsRester;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;
import com.lezo.rest.data.DataRestable;
import com.lezo.rest.data.RestFile;
import com.lezo.rest.data.RestList;

public class DataFileProducer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(DataFileProducer.class);
	private static final String DIR_SEPARATOR = "/";
	private ThreadPoolExecutor executor = ExecutorUtils.getFileConsumeExecutor();
	private DirectoryTracker tracker;

	public DataFileProducer(DirectoryTracker tracker) {
		this.tracker = tracker;
	}

	@Override
	public void run() {
		DirectoryDescriptor descriptor = this.tracker.getDescriptor();
		Lock locker = DirectoryLockUtils.findLock(descriptor.getDirectoryKey());
		if (locker == null) {
			throw new IllegalArgumentException("can not get locker:" + descriptor.getDirectoryKey());
		}
		if (locker.tryLock()) {
			try {
				locker.lock();
				doWork(descriptor);
			} catch (Exception e) {
				logger.warn("do file produce.cause:", e);
			} finally {
				locker.unlock();
			}
		} else {
			logger.warn(descriptor.getDirectoryKey() + " is running...");
		}
	}

	private void doWork(DirectoryDescriptor descriptor) throws Exception {
		ClientRest clientRest = ClientRestFactory.getInstance().get(descriptor.getBucketName(), descriptor.getDomain(),
				5, 15 * 1000);
		if (clientRest == null) {
			throw new IllegalArgumentException("can not get ClientRest:" + descriptor.getBucketName() + "."
					+ descriptor.getDomain());
		}
		DataRestable rester = clientRest.getRester();
		int limit = 100;
		int count = 0;
		int listTimes = 0;
		Map<String, String> paramMap = this.tracker.getParamMap();
		String limitChars = paramMap.get("limit");
		int fromCount = 0;
		if (rester instanceof BaiduPcsRester) {
			if (StringUtils.isNotEmpty(limitChars)) {
				fromCount = Integer.valueOf(limitChars.split("-")[1]);
			}
		} else {
			paramMap.put("limit", "" + limit);
		}
		long startStamp = this.tracker.getStamp();
		while (true) {
			listTimes++;
			long startMills = System.currentTimeMillis();
			if (rester instanceof BaiduPcsRester) {
				paramMap.put("limit", fromCount + "-" + (fromCount + limit));
			}
			RestList restList = rester.listFiles(descriptor.getDataPath(), paramMap);
			long costMills = System.currentTimeMillis() - startMills;
			if (CollectionUtils.isNotEmpty(restList.getDataList())) {
				fromCount += restList.getDataList().size();
				List<RestFile> acceptList = getAccepts(restList.getDataList(), this.tracker.getStamp());
				if (!CollectionUtils.isEmpty(acceptList)) {
					count += acceptList.size();
					logger.info("directoryKey:" + this.tracker.getDescriptor().getDirectoryKey() + ",stamp:"
							+ this.tracker.getStamp() + ",acceptSum:" + count + ",result:"
							+ restList.getDataList().size() + ",accept:" + acceptList.size() + ",listTimes:"
							+ listTimes + ",listCost:" + costMills);
					createDataFileConsumer(descriptor, acceptList);
					this.tracker.setFileCount(this.tracker.getFileCount() + acceptList.size());
					this.tracker.setStamp(getMaxStamp(acceptList));
					if (this.tracker.getStamp() - startStamp >= 10 * 60 * 1000) {
						break;
					}
				} else {
					// throw new RuntimeException(descriptor.getBucketName() +
					// ":" + this.tracker.getDescriptor().getDirectoryKey() +
					// ",listCount:" + restList.getDataList().size() +
					// ",but accept 0");
				}
			} else {
				logger.warn("list empty.directoryKey:" + this.tracker.getDescriptor().getDirectoryKey() + ",listTimes:"
						+ listTimes + ",listCost:" + costMills);
			}
			if (restList.isEOF()) {
				logger.info("list to EOF.directoryKey:" + this.tracker.getDescriptor().getDirectoryKey() + ",maxCount:"
						+ listTimes + ",listCost:" + costMills);
				break;
			} else {
				paramMap.put("marker", restList.getMarker());
			}
			TimeUnit.SECONDS.sleep(1);
		}
		logger.info("directoryKey:" + this.tracker.getDescriptor().getDirectoryKey() + ", :" + this.tracker.getStamp()
				+ ",totalCount:" + this.tracker.getFileCount() + ",newCount:" + count);
	}

	private void createDataFileConsumer(DirectoryDescriptor descriptor, List<RestFile> acceptList) {
		String type = getTypeFromPath(descriptor.getDataPath());
		int addCount = 0;
		for (RestFile item : acceptList) {
			try {
				DataFileWrapper dataFileWrapper = new DataFileWrapper();
				dataFileWrapper.setBucketName(descriptor.getBucketName());
				dataFileWrapper.setDomain(descriptor.getDomain());
				dataFileWrapper.setItem(item);
				executor.execute(new DataFileConsumer(type, dataFileWrapper));
				addCount++;
			} catch (Exception e) {
				logger.warn("File:" + item.getPath() + ",cause:", e);
			}
		}
		RestFile firstFile = acceptList.get(0);
		logger.info("Get new file,addCount:" + addCount + ",total:" + acceptList.size() + ",first path:"
				+ firstFile.getPath());
	}

	private long getMaxStamp(List<RestFile> acceptList) {
		long max = 0L;
		for (RestFile item : acceptList) {
			if (max < item.getCreateTime()) {
				max = item.getCreateTime();
			}
		}
		return max;
	}

	private List<RestFile> getAccepts(List<RestFile> restList, long stamp) {
		if (CollectionUtils.isEmpty(restList)) {
			return Collections.emptyList();
		}
		List<RestFile> itemList = new ArrayList<RestFile>(restList.size());
		for (RestFile rs : restList) {
			if (stamp > rs.getCreateTime()) {
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
