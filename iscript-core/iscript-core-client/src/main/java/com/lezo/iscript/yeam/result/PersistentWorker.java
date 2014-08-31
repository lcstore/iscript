package com.lezo.iscript.yeam.result;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.rest.RestUtils;
import com.lezo.iscript.yeam.storage.ResultFutureStorager;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.qiniu.api.io.PutRet;

public class PersistentWorker implements Runnable {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(PersistentWorker.class);
	private static final String CHARSET_NAME = "UTF-8";
	private String rootPath = "iscript";
	private String type;
	private String batchId;
	private List<ResultWritable> copyList;

	public PersistentWorker(String type, String batchId, List<ResultWritable> copyList) {
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
				ResultWritable fileWritable = add2Disk(copyList);
				Future<ResultWritable> data = new DoneFuture<ResultWritable>(fileWritable);
				ResultFutureStorager.getInstance().getStorageBuffer().add(data);
				break;
			} catch (Exception e) {
				retry++;
				logger.warn("", e);
			}
		}
	}

	private ResultWritable add2Disk(List<ResultWritable> rWritables) throws Exception {
		String path = getFilePath();
		InputStream reader = toInputStream(rWritables);
		File destFile = new File(path);
		PutRet ret = (PutRet) RestUtils.doRestCallBack(destFile, reader, RestUtils.QINIU_CALL_BACK);
		if (ret.ok()) {
			logger.info(String.format("Succss to add:%s", path));
			ResultWritable fileWritable = getFileWritable(rWritables, destFile);
			return fileWritable;
		}
		throw new IOException(ret.response, ret.exception);
	}

	private InputStream toInputStream(List<ResultWritable> rWritables) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream destStream = null;
		try {
			GZIPOutputStream gzos = new GZIPOutputStream(bos);
			for (ResultWritable rWritable : rWritables) {
				String result = rWritable.getResult();
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

	private ResultWritable getFileWritable(List<ResultWritable> rWritables, File destFile) {
		ResultWritable rWritable = new ResultWritable();
		rWritable.setType("FileMessage");
		JSONObject gObject = new JSONObject();
		JSONObject rsObject = new JSONObject();
		JSONUtils.put(gObject, "rs", rsObject);
		JSONArray jArray = new JSONArray();
		for (ResultWritable rw : rWritables) {
			jArray.put(rw.getTaskId());
		}
		JSONUtils.put(rsObject, "idList", jArray);
		JSONUtils.put(rsObject, "file", destFile);
		
		JSONObject argsObject = new JSONObject();
		JSONUtils.put(gObject, "args", argsObject);
		JSONUtils.put(argsObject, "bid", destFile.getParentFile().getName());
		JSONUtils.put(argsObject, "type", destFile.getParentFile().getParentFile().getName());
		rWritable.setResult(gObject.toString());
		return rWritable;
	}
}
