package com.lezo.iscript.yeam.resultmgr;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.URLUtils;
import com.qiniu.api.rsf.ListItem;
import com.qiniu.api.rsf.ListPrefixRet;
import com.qiniu.api.rsf.RSFClient;
import com.qiniu.api.rsf.RSFEofException;

public class DataLineProducer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(DataLineProducer.class);
	private static final String DIR_SEPARATOR = "/";
	private static final DefaultHttpClient CLIENT = HttpClientUtils.createHttpClient();
	private static final String CHARSET_NAME = "UTF-8";
	private Mac mac = SpringBeanUtils.getBean(Mac.class);
	private ThreadPoolExecutor executor = (ThreadPoolExecutor) SpringBeanUtils.getBean("dataConsumeExecutor");
	private String domain = "istore.qiniudn.com";
	private final String dataPath;
	private final Date stamp;

	public DataLineProducer(String dataPath, Date stamp) {
		super();
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
			ret = client.listPrifix("istore", key, marker, limit);
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
		downDataToCumsume(type, itemList);
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

	private void downDataToCumsume(String type, List<ListItem> itemList) {
		for (ListItem item : itemList) {
			try {
				List<String> dataList = downData(item);
				add2Cunsume(type, dataList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void add2Cunsume(String type, List<String> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		for (String data : dataList) {
			executor.execute(new DataLineConsumer(type, data));
		}

	}

	private List<String> downData(ListItem item) throws EncoderException, AuthException {
		String baseUrl = URLUtils.makeBaseUrl(domain, item.key);
		GetPolicy getPolicy = new GetPolicy();
		String downloadUrl = getPolicy.makeRequest(baseUrl, mac);
		HttpGet fileGet = new HttpGet(downloadUrl);
		InputStream inStream = null;
		try {
			HttpResponse res = CLIENT.execute(fileGet);
			inStream = res.getEntity().getContent();
			return toDataList(inStream);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		} finally {
			IOUtils.closeQuietly(inStream);
			if (fileGet != null && !fileGet.isAborted()) {
				fileGet.abort();
			}
		}
	}

	private List<String> toDataList(InputStream inStream) throws Exception {
		if (inStream == null) {
			return Collections.emptyList();
		}
		GZIPInputStream gis = new GZIPInputStream(inStream);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] tmp = new byte[1024]; // Rough estimate
		int len = -1;
		while ((len = gis.read(tmp)) > -1) {
			bos.write(tmp, 0, len);
		}
		bos.flush();
		byte[] byteArray = bos.toByteArray();
		bos.close();
		return toStringList(byteArray);
	}

	private List<String> toStringList(byte[] byteArray) throws Exception {
		String fileData = new String(byteArray, CHARSET_NAME);
		StringTokenizer tokenizer = new StringTokenizer(fileData, "\n");
		List<String> stringList = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			stringList.add(tokenizer.nextToken());
		}
		return stringList;
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
