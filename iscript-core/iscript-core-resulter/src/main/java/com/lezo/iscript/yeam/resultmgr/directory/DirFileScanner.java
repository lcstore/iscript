package com.lezo.iscript.yeam.resultmgr.directory;

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

import com.lezo.iscript.yeam.resultmgr.DataFileConsumer;
import com.lezo.iscript.yeam.resultmgr.DataFileWrapper;
import com.lezo.iscript.yeam.resultmgr.ExecutorUtils;
import com.lezo.rest.data.BaiduPcsRester;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;
import com.lezo.rest.data.DataRestable;
import com.lezo.rest.data.RestFile;
import com.lezo.rest.data.RestList;

public class DirFileScanner implements Runnable {
	private static final long INTERVAL_BEFORE = 10 * 60 * 1000;
	private static Logger logger = LoggerFactory.getLogger(DirFileScanner.class);
	private ThreadPoolExecutor executor = ExecutorUtils.getFileConsumeExecutor();
	private DirSummary dirStream;

	public DirFileScanner(DirSummary dirStream) {
		super();
		this.dirStream = dirStream;
	}

	@Override
	public void run() {
		Lock locker = dirStream.getLocker();
		if (locker.tryLock()) {
			try {
				locker.lock();
				doWork();
			} catch (Exception e) {
				logger.warn("do file produce.cause:", e);
			} finally {
				locker.unlock();
			}
		} else {
			logger.warn(dirStream.getDirBean().toDirKey() + " is running...");
		}
	}

	private void doWork() throws Exception {
		DirMeta dirBean = dirStream.getDirBean();
		ClientRest clientRest = ClientRestFactory.getInstance().get(dirBean.getBucket(), dirBean.getDomain(), 5,
				15 * 1000);
		if (clientRest == null) {
			throw new IllegalArgumentException("can not get ClientRest:" + dirBean.getBucket() + "."
					+ dirBean.getDomain());
		}
		DataRestable rester = clientRest.getRester();
		int limit = 100;
		int count = 0;
		int listTimes = 0;
		Map<String, String> paramMap = dirStream.getParamMap();
		String limitChars = paramMap.get("limit");
		int fromCount = 0;
		if (rester instanceof BaiduPcsRester) {
			if (StringUtils.isNotEmpty(limitChars)) {
				fromCount = Integer.valueOf(limitChars.split("-")[1]);
			}
		} else {
			paramMap.put("limit", "" + limit);
		}
		while (!dirStream.isDone()) {
			listTimes++;
			long startMills = System.currentTimeMillis();
			if (rester instanceof BaiduPcsRester) {
				paramMap.put("limit", fromCount + "-" + (fromCount + limit));
			}
			RestList restList = rester.listFiles(dirBean.toDirPath(), paramMap);
			long costMills = System.currentTimeMillis() - startMills;
			List<RestFile> acceptList = null;
			if (CollectionUtils.isNotEmpty(restList.getDataList())) {
				fromCount += restList.getDataList().size();
				acceptList = getAccepts(restList.getDataList());
				if (!CollectionUtils.isEmpty(acceptList)) {
					count += acceptList.size();
					createDataFileConsumer(acceptList);
					dirStream.setCount(dirStream.getCount() + acceptList.size());
					dirStream.setToStamp(getMaxStamp(acceptList));
					logger.info("directoryKey:" + dirBean.toDirKey() + ",stamp:" + dirStream.getToStamp()
							+ ",acceptSum:" + count + ",result:" + restList.getDataList().size() + ",accept:"
							+ acceptList.size() + ",listTimes:" + listTimes + ",listCost:" + costMills + ",fromStamp:"
							+ dirStream.getFromStamp() + ",toStamp:" + dirStream.getToStamp());
				} else {
					long maxStamp = getMaxStamp(restList.getDataList());
					logger.warn("directoryKey:" + dirBean.toDirKey() + ",listCount:" + restList.getDataList().size()
							+ ",but accept 0,Summary stamp:" + dirStream.getToStamp() + ",listMaxStamp:" + maxStamp
							+ ",limit:" + paramMap.get("limit"));
				}
			} else {
				logger.warn("list empty.directoryKey:" + dirBean.toDirKey() + ",listTimes:" + listTimes + ",listCost:"
						+ costMills);
			}
			if (restList.isEOF()) {
				dirStream.setDone(true);
				// handle empty directory
				if (CollectionUtils.isEmpty(acceptList)) {
					dirStream.setToStamp(dirStream.getToStamp() + 1);
				}
				logger.info("list to EOF.directoryKey:" + dirBean.toDirKey() + ",listTimes:" + listTimes + ",maxCount:"
						+ count + ",listCost:" + costMills + ",fromStamp:" + dirStream.getFromStamp() + ",toStamp:"
						+ dirStream.getToStamp());
				break;
			} else {
				paramMap.put("marker", restList.getMarker());
			}
			TimeUnit.SECONDS.sleep(1);
		}
		logger.info("directoryKey:" + dirBean.toDirKey() + ", fromStamp:" + dirStream.getFromStamp() + ",toStamp:"
				+ dirStream.getToStamp() + ",totalCount:" + dirStream.getCount() + ",newCount:" + count);
	}

	private void createDataFileConsumer(List<RestFile> acceptList) {
		DirMeta dirBean = dirStream.getDirBean();
		String type = dirBean.getType();
		int addCount = 0;
		for (RestFile item : acceptList) {
			try {
				DataFileWrapper dataFileWrapper = new DataFileWrapper();
				dataFileWrapper.setBucketName(dirBean.getBucket());
				dataFileWrapper.setDomain(dirBean.getDomain());
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

	private List<RestFile> getAccepts(List<RestFile> restList) {
		if (CollectionUtils.isEmpty(restList)) {
			return Collections.emptyList();
		}
		long fromStamp = dirStream.getFromStamp() - INTERVAL_BEFORE;
		long toStamp = dirStream.getToStamp();
		List<RestFile> itemList = new ArrayList<RestFile>(restList.size());
		for (RestFile rs : restList) {
			if (dirStream.getFromStamp() == toStamp && rs.getCreateTime() >= fromStamp) {
				itemList.add(rs);
			} else if (dirStream.getFromStamp() != toStamp && rs.getCreateTime() >= toStamp) {
				itemList.add(rs);
			}
		}
		return itemList;
	}
}
