package com.lezo.iscript.yeam.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import sun.util.calendar.ZoneInfo;

import com.lezo.iscript.yeam.mina.utils.ServerTimeUtils;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;

public class PersistentWorker implements Runnable {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(PersistentWorker.class);
	private static final String CHARSET_NAME = "UTF-8";
	private static final String PATH_SEPARATPR = File.separator;
	private String rootPath = "iscript";
	private String type;
	private String batchId;
	private String bucket;
	private String domain;
	private List<String> copyList;

	public PersistentWorker(String type, String batchId, String bucket, String domain, List<String> copyList) {
		super();
		this.type = type;
		this.batchId = batchId;
		this.bucket = bucket;
		this.domain = domain;
		this.copyList = copyList;
	}

	@Override
	public void run() {
		int retry = 0;
		while (retry < 3) {
			try {
				upload2Cloud(copyList);
				break;
			} catch (Exception e) {
				retry++;
				logger.warn("", e);
			}
		}
	}

	private void upload2Cloud(List<String> rWritables) throws Exception {
		ClientRest clientRest = ClientRestFactory.getInstance().get(this.bucket, this.domain);
		if (clientRest == null) {
			logger.error("error,can not get ClientRest.bucket:" + this.bucket + ",domain:" + this.domain + ",miss data:" + rWritables.size());
		} else {
			String targetPath = getFilePath();
			byte[] byteArray = toGzipByteArray(rWritables);
			try {
				if (clientRest.getRester().upload(targetPath, byteArray)) {
					logger.info(String.format("result.add[%s]:%s", this.bucket + "." + this.domain, targetPath));
				}
			} catch (Exception e) {
				logger.error(String.format("result.miss[%s]:%s,", this.bucket + "." + this.domain, targetPath), e);
				throw e;
			}
		}
	}

	private byte[] toGzipByteArray(List<String> rWritables) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			GZIPOutputStream gzos = new GZIPOutputStream(bos);
			for (String result : rWritables) {
				result += "\n";
				gzos.write(result.getBytes(CHARSET_NAME));
			}
			gzos.close();
			return bos.toByteArray();
		} finally {
			IOUtils.closeQuietly(bos);
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
		c.setTimeInMillis(ServerTimeUtils.getTimeMills());
		c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		String toDay = "" + c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		toDay += month < 10 ? "0" + month : month;
		toDay += day < 10 ? "0" + day : day;
		StringBuilder sb = new StringBuilder();
		sb.append(rootPath);
		sb.append(PATH_SEPARATPR);
		sb.append(toDay);
		sb.append(PATH_SEPARATPR);
		sb.append(type);
		sb.append(PATH_SEPARATPR);
		sb.append(batchId);
		sb.append(PATH_SEPARATPR);
		sb.append(type);
		sb.append(".");
		sb.append(toDay);
		sb.append(".");
		sb.append(c.getTimeInMillis());
		sb.append(".gz");
		return sb.toString();
	}
}