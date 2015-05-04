package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;

public class DataFileConsumer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(DataFileConsumer.class);
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
			// logger.info("dataPath:"+dataFileWrapper.getItem().getPath()+",dataLine:"+dataLineList.size());
		} catch (Exception e) {
			logger.info("dataPath:" + dataFileWrapper.getItem().getPath() + ",cause:", e);
		}
		if (CollectionUtils.isNotEmpty(dataLineList)) {
			ThreadPoolExecutor dataConsumeExecutor = ExecutorUtils.getDataConsumeExecutor();
			logger.info("ready to consume dataline.count:" + dataLineList.size());
			for (String dataLine : dataLineList) {
				dataConsumeExecutor.execute(new DataLineConsumer(type, dataLine));
			}
		}
	}

	private List<String> downData() throws Exception {
		ClientRest clientRest = ClientRestFactory.getInstance().get(dataFileWrapper.getBucketName(),
				dataFileWrapper.getDomain(), 5, 15 * 1000);
		if (clientRest == null) {
			logger.warn("can not get ClientRest.bucket:" + dataFileWrapper.getBucketName() + ",domain:"
					+ dataFileWrapper.getDomain());
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
}
