package com.lezo.iscript.yeam.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.lezo.iscript.yeam.rest.RestUtils;
import com.qiniu.api.io.PutRet;

public class PersistentWorker implements Runnable {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(PersistentWorker.class);
	private static final String CHARSET_NAME = "UTF-8";
	private String rootPath = "iscript";
	private String type;
	private String batchId;
	private List<String> copyList;

	public PersistentWorker(String type, String batchId, List<String> copyList) {
		super();
		this.type = type;
		this.batchId = batchId;
		this.copyList = copyList;
	}

	@Override
	public void run() {
		int retry = 0;
		while (retry < 3) {
			try {
				add2Disk(copyList);
				break;
			} catch (Exception e) {
				retry++;
				logger.warn("", e);
			}
		}
	}

	private void add2Disk(List<String> rWritables) throws Exception {
		String path = getFilePath();
		InputStream reader = toInputStream(rWritables);
		File destFile = new File(path);
		PutRet ret = (PutRet) RestUtils.doRestCallBack(destFile, reader, RestUtils.QINIU_CALL_BACK);
		if (ret.ok()) {
			logger.info(String.format("Succss to add:%s", path));
		}else {
			throw new IOException(ret.response, ret.exception);
		}
	}

	private InputStream toInputStream(List<String> rWritables) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream destStream = null;
		try {
			GZIPOutputStream gzos = new GZIPOutputStream(bos);
			for (String result : rWritables) {
				result += "\n";
				gzos.write(result.getBytes(CHARSET_NAME));
			}
			gzos.close();
			byte[] bytes = bos.toByteArray();
			destStream = new ByteArrayInputStream(bytes);
			// destStream = new ZipInputStream(bis);
		} finally {
			IOUtils.closeQuietly(bos);
		}
		return destStream;
	}

	private String getFilePath() {
		Calendar c = Calendar.getInstance();
		String toDay = "" + c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		toDay += month < 10 ? "0" + month : month;
		toDay += day < 10 ? "0" + day : day;
		StringBuilder sb = new StringBuilder();
		sb.append(rootPath);
		sb.append(File.separator);
		sb.append(toDay);
		sb.append(File.separator);
		sb.append(type);
		sb.append(File.separator);
		sb.append(batchId);
		sb.append(File.separator);
		sb.append(type);
		sb.append(".");
		sb.append(toDay);
		sb.append(".");
		sb.append(System.currentTimeMillis());
		sb.append(".gz");
		return sb.toString();
	}
}
