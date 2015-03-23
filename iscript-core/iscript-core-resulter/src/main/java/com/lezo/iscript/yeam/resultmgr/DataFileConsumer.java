package com.lezo.iscript.yeam.resultmgr;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;

public class DataFileConsumer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(DataFileConsumer.class);
	private static final String CHARSET_NAME = "UTF-8";
	private String type;
	private DataFileWrapper dataFileWrapper;

	public DataFileConsumer(String type, DataFileWrapper dataFileWrapper) {
		super();
		this.type = type;
		this.dataFileWrapper = dataFileWrapper;
	}

	@Override
	public void run() {
		List<String> dataLineList = null;
		try {
			dataLineList = downData();
			logger.info("dataPath:"+dataFileWrapper.getItem().getPath()+",dataLine:"+dataLineList.size());
		} catch (Exception e) {
			logger.info("dataPath:"+dataFileWrapper.getItem().getPath()+",cause:",e);
		}
		if (CollectionUtils.isNotEmpty(dataLineList)) {
			ThreadPoolExecutor dataConsumeExecutor = (ThreadPoolExecutor) SpringBeanUtils.getBean("dataConsumeExecutor");
			for (String dataLine : dataLineList) {
				dataConsumeExecutor.execute(new DataLineConsumer(type, dataLine));
			}
		}
	}

	private List<String> downData() throws Exception {
		ClientRest clientRest = ClientRestFactory.getInstance().get(dataFileWrapper.getBucketName(), dataFileWrapper.getDomain(), 5, 15 * 1000);
		if (clientRest == null) {
			logger.warn("can not get ClientRest.bucket:" + dataFileWrapper.getBucketName() + ",domain:" + dataFileWrapper.getDomain());
			return Collections.emptyList();
		}
		String content = clientRest.getRester().download(dataFileWrapper.getItem().getPath());
		StringTokenizer tokenizer = new StringTokenizer(content, "\n");
		List<String> lineList = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
			lineList.add(tokenizer.nextElement().toString());
		}
		return lineList;
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
}
