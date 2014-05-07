package com.lezo.iscript.yeam.resulter.handle;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ResultConstant;
import com.lezo.iscript.yeam.service.TaskRemoteService;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.PutPolicy;

public class QiniuResultHandler implements ResultHandle {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private String bucketName = "istore";
	private String targetDir = "iscript";
	private String encoding = "UTF-8";
	private String tempDir;
	private Mac mac;
	private TaskRemoteService taskRemoteService;

	@Override
	public void handle(ResultWritable resultWritable) {
		if (resultWritable.getStatus() != ResultConstant.RESULT_SUCCESS) {
			return;
		}
		String uptoken = getUptoken();
		if (uptoken == null) {
			return;
		}
		String name = getFileName(resultWritable);
		File source = new File(tempDir, name);
		writeFile(source, resultWritable);
		PutExtra extra = new PutExtra();
		String key = targetDir + File.separator + name;
		key = toQiniuKey(key);
		String localFile = source.getAbsolutePath();
		PutRet ret = IoApi.putFile(uptoken, key, localFile, extra);
		if (ret.ok()) {
			source.delete();
		}
		log.info("temp source:" + source + ",dest qiniu bucket[" + bucketName + "].key:" + key);
		log.info("send to qiniu.status:" + ret.ok() + ",respone:" + ret.response + ",exception:" + ret.exception);
		nextTask(resultWritable);
	}

	private void nextTask(ResultWritable resultWritable) {
		JSONObject rsObject = JSONUtils.getJSONObject(resultWritable.getResult());
		rsObject = JSONUtils.getJSONObject(JSONUtils.getString(rsObject, "rs"));
		if (rsObject == null) {
			return;
		}
		JSONObject nextObject = JSONUtils.get(rsObject, "next", JSONObject.class);
		if (nextObject == null) {
			return;
		}
		JSONObject argsObject = JSONUtils.get(rsObject, "args", JSONObject.class);
		TaskWritable taskWritable = new TaskWritable();
		addArgs(taskWritable, argsObject);
		addArgs(taskWritable, nextObject);
		List<TaskWritable> taskList = new ArrayList<TaskWritable>();
		taskList.add(taskWritable);
		taskRemoteService.addTasks(taskList);
		log.info("next:" + new JSONObject(taskWritable.getArgs()));
	}

	private void addArgs(TaskWritable taskWritable, JSONObject argsObject) {
		if (argsObject == null) {
			return;
		}
		Iterator<?> it = argsObject.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			Object value = JSONUtils.getObject(argsObject, key);
			taskWritable.put(key, value);
		}
	}

	private String toQiniuKey(String key) {
		String newKey = key.replace("\\", "/");
		try {
			newKey = new String(newKey.getBytes(), encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newKey;
	}

	private void writeFile(File source, ResultWritable resultWritable) {
		JSONObject rsObject = new JSONObject();
		JSONUtils.put(rsObject, "tid", resultWritable.getTaskId());
		JSONUtils.put(rsObject, "result", resultWritable.getResult());
		String data = rsObject.toString();
		try {
			FileUtils.writeStringToFile(source, data, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getUptoken() {
		PutPolicy putPolicy = new PutPolicy(bucketName);
		String uptoken = null;
		try {
			uptoken = putPolicy.token(mac);
		} catch (Exception e) {
			log.warn("", e);
		}
		return uptoken;
	}

	private String getFileName(ResultWritable resultWritable) {
		JSONObject rsObject = JSONUtils.getJSONObject(resultWritable.getResult());
		String rsContent = JSONUtils.get(rsObject, "rs", String.class);
		rsObject = JSONUtils.getJSONObject(rsContent);
		JSONObject argsObject = JSONUtils.get(rsObject, "args", JSONObject.class);
		String type = JSONUtils.get(argsObject, "type", String.class);
		StringBuilder sb = new StringBuilder();
		if (argsObject != null) {
			sb.append(type);
			sb.append(".");
			String sbid = JSONUtils.get(argsObject, "bid", String.class);
			if (sbid != null) {
				sb.append(sbid);
			}
		}
		sb.append(".");
		sb.append(System.currentTimeMillis());
		String dir = type == null ? "unkown" : type;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String sToday = sdf.format(new Date());
		String name = sb.toString();
		return dir + File.separator + sToday + File.separator + name;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	public void setMac(Mac mac) {
		this.mac = mac;
	}

	public void setTaskRemoteService(TaskRemoteService taskRemoteService) {
		this.taskRemoteService = taskRemoteService;
	}

}
