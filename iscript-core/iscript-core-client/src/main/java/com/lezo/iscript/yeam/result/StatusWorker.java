package com.lezo.iscript.yeam.result;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipInputStream;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.mina.SessionSender;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.rest.RestUtils;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.qiniu.api.io.PutRet;

public class StatusWorker implements Runnable {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(StatusWorker.class);
	private static final String CHARSET_NAME = "UTF-8";
	private String rootPath = "iscript";
	private List<ResultWritable> copyList;

	public StatusWorker(List<ResultWritable> copyList) {
		super();
		this.copyList = copyList;
	}

	@Override
	public void run() {
		Map<String, List<ResultWritable>> fileMap = new HashMap<String, List<ResultWritable>>();
		for (ResultWritable rWritable : copyList) {
			String key = rWritable.getType() + "-" + rWritable.getTaskId();
			List<ResultWritable> rWritables = fileMap.get(key);
			if (rWritables == null) {
				rWritables = new ArrayList<ResultWritable>();
				fileMap.put(key, rWritables);
			}
			rWritables.add(rWritable);
		}
		List<ResultWritable> messageList = new ArrayList<ResultWritable>();
		for (Entry<String, List<ResultWritable>> entry : fileMap.entrySet()) {
			try {
				ResultWritable msgWritable = add2Disk(entry.getValue());
				if (msgWritable == null) {
					messageList.add(msgWritable);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sendRequest(messageList);
	}

	private void sendRequest(List<ResultWritable> rwList) {
		if (!rwList.isEmpty()) {
			BatchIterator<ResultWritable> it = new BatchIterator<ResultWritable>(rwList, 10);
			while (it.hasNext()) {
				IoRequest ioRequest = new IoRequest();
				JSONObject hObject = HeaderUtils.getHeader();
				ioRequest.setHeader(hObject.toString());
				List<ResultWritable> subList = new ArrayList<ResultWritable>(it.next());
				ioRequest.setData(subList);
				SessionSender.getInstance().send(ioRequest);
			}
		}
	}

	private ResultWritable add2Disk(List<ResultWritable> rWritables) throws Exception {
		if (CollectionUtils.isEmpty(rWritables)) {
			return null;
		}
		ResultWritable firstWritable = rWritables.get(0);
		String path = getFilePath(firstWritable);
		InputStream reader = toInputStream(rWritables);
		File destFile = new File(path);
		PutRet ret = (PutRet) RestUtils.doRestCallBack(destFile, reader, RestUtils.QINIU_CALL_BACK);
		if (ret.ok()) {
			logger.info(String.format("Succss to add:%s", path));
			ResultWritable fileWritable = getFileWritable(rWritables, destFile);
			return fileWritable;
		} else {
			logger.warn(String.format("Fail to add:%s,cause:%s", path, ret.response));
		}
		return null;
	}

	private ResultWritable getFileWritable(List<ResultWritable> rWritables, File destFile) {
		ResultWritable rWritable = new ResultWritable();
		rWritable.setType("FileMessage");
		JSONObject jObject = new JSONObject();
		JSONArray jArray = new JSONArray();
		for (ResultWritable rw : rWritables) {
			jArray.put(rw.getTaskId());
		}
		JSONUtils.put(jObject, "idList", jArray);
		JSONUtils.put(jObject, "bid", destFile.getParentFile().getName());
		JSONUtils.put(jObject, "type", destFile.getParentFile().getParentFile().getName());
		rWritable.setResult(jObject.toString());
		return rWritable;
	}

	private InputStream toInputStream(List<ResultWritable> rWritables) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream destStream = null;
		try {
			for (ResultWritable rWritable : rWritables) {
				String result = rWritable.getResult();
				result += "\n";
				try {
					bos.write(result.getBytes(CHARSET_NAME));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			bos.flush();
			InputStream bis = new ByteArrayInputStream(bos.toByteArray());
			destStream = new ZipInputStream(bis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			org.apache.commons.io.IOUtils.closeQuietly(bos);
		}
		return destStream;
	}

	private String getFilePath(ResultWritable firstWritable) {
		Calendar c = Calendar.getInstance();
		String toDay = "" + c.get(Calendar.YEAR) + c.get(Calendar.DAY_OF_MONTH);
		String batchId = getBatchId(firstWritable);
		StringBuilder sb = new StringBuilder();
		sb.append(rootPath);
		sb.append(File.separator);
		sb.append(toDay);
		sb.append(File.separator);
		sb.append(firstWritable.getType());
		sb.append(File.separator);
		sb.append(batchId);
		sb.append(firstWritable.getType() + "." + toDay + "." + System.currentTimeMillis());
		return sb.toString();
	}

	private String getBatchId(ResultWritable firstWritable) {
		JSONObject rsObject = JSONUtils.getJSONObject(firstWritable.getResult());
		if (rsObject == null) {
			return null;
		}
		JSONObject argsObject = JSONUtils.get(rsObject, "args");
		return argsObject == null ? null : JSONUtils.getString(argsObject, "bid");
	}
}
