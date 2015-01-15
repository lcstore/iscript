package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rsf.ListItem;
import com.qiniu.api.rsf.ListPrefixRet;
import com.qiniu.api.rsf.RSFClient;
import com.qiniu.api.rsf.RSFEofException;

public class DataFileProducer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(DataFileProducer.class);
	private static final String DIR_SEPARATOR = "/";
	private Mac mac = SpringBeanUtils.getBean(Mac.class);
	private ThreadPoolExecutor executor = (ThreadPoolExecutor) SpringBeanUtils.getBean("fileConsumeExecutor");
	private String bucketName;
	private String domain = ".qiniudn.com";
	private final String dataPath;
	private final Date stamp;

	public DataFileProducer(String bucketName, String dataPath, Date stamp) {
		super();
		this.bucketName = bucketName;
		this.dataPath = dataPath;
		this.stamp = stamp;
	}

	@Override
	public void run() {
		String type = getTypeFromPath(dataPath);
		CacheObject cacheObject = newIfAbsent(dataPath);
		String key = cacheObject.getKey();
		RSFClient client = new RSFClient(mac);
		String marker = cacheObject.getValue().toString();
		List<ListItem> itemList = new ArrayList<ListItem>();
		ListPrefixRet ret = null;
		int limit = 50;
		int retry = 0;
		while (true) {
			ret = client.listPrifix(this.bucketName, key, marker, limit);
			if (ret.statusCode >= 200 && ret.statusCode < 300) {
				marker = ret.marker;
				addAccepts(ret.results, cacheObject, itemList);
				if (!CollectionUtils.isEmpty(ret.results)) {
					itemList.addAll(ret.results);
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
		if (marker != null && !marker.equals(cacheObject.getValue().toString())) {
			cacheObject.setValue(marker);
		}
		createDataFileConsumer(type, itemList);
	}

	private void createDataFileConsumer(String type, List<ListItem> itemList) {
		for (ListItem item : itemList) {
			try {
				DataFileWrapper dataFileWrapper = new DataFileWrapper();
				dataFileWrapper.setBucketName(this.bucketName);
				dataFileWrapper.setDomain(this.domain);
				dataFileWrapper.setItem(item);
				dataFileWrapper.setMac(mac);
				executor.execute(new DataFileConsumer(type, dataFileWrapper));
			} catch (Exception e) {
				logger.warn("File:" + item.key + ",cause:", e);
			}
		}

	}

	private void addAccepts(List<ListItem> results, CacheObject cacheObject, List<ListItem> itemList) {
		if (CollectionUtils.isEmpty(results)) {
			return;
		}
		long maxStamp = cacheObject.getStamp();
		for (ListItem rs : results) {

			if (maxStamp < rs.putTime) {
				itemList.add(rs);
				maxStamp = rs.putTime;
			}
		}
		if (cacheObject.getStamp() != maxStamp) {
			cacheObject.setStamp(maxStamp);
		}
	}

	private String getTypeFromPath(String dataPath) {
		int fromIndex = dataPath.indexOf(DIR_SEPARATOR) + 1;
		fromIndex = dataPath.indexOf(DIR_SEPARATOR, fromIndex) + 1;
		int toIndex = dataPath.indexOf(DIR_SEPARATOR, fromIndex);
		return dataPath.substring(fromIndex, toIndex);
	}

	private CacheObject newIfAbsent(String dataPath) {
		CacheObjectController controller = CacheObjectController.getInstance();
		CacheObject cacheObject = controller.getValidValue(dataPath);
		if (cacheObject == null) {
			synchronized (controller) {
				cacheObject = new CacheObject(dataPath, "", this.stamp.getTime(), controller.getNextTimeOut());
				controller.addValidValue(dataPath, cacheObject);
			}
		}
		return cacheObject;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setMac(Mac mac) {
		this.mac = mac;
	}

}
