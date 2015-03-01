package com.lezo.iscript.yeam.resulter.handle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.pcs.BaiduPCSActionInfo.PCSFileInfoResponse;
import com.baidu.pcs.BaiduPCSClient;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class PcsResultHandler implements ResultHandle {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private BaiduPCSClient pcsClient;
	private String pcsPath;
	private String tempDir;

	@Override
	public void handle(ResultWritable resultWritable) {
		if (resultWritable.getStatus() != ResultWritable.RESULT_SUCCESS) {
			return;
		}
		String name = getFileName(resultWritable);
		File source = new File(tempDir, name);
		File target = new File(pcsPath, name);
		PCSFileInfoResponse res = pcsClient.uploadFile(source.getAbsolutePath(), target.getAbsolutePath());
	}

	private String getFileName(ResultWritable resultWritable) {
		JSONObject rsObject = JSONUtils.getJSONObject(resultWritable.getResult());
		JSONObject argsObject = (JSONObject) JSONUtils.getObject(rsObject, "args");
		String type = (String) JSONUtils.getObject(argsObject, "type");
		StringBuilder sb = new StringBuilder();
		if (argsObject != null) {
			sb.append(type);
			sb.append(".");
			sb.append(JSONUtils.getObject(argsObject, "bid"));
		}
		sb.append(".");
		sb.append(System.currentTimeMillis());
		String dir = type == null ? "unkown" : type;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String sToday = sdf.format(new Date());
		String name = sb.toString();
		return dir + File.separator + sToday + name;
	}

	public void setPcsClient(BaiduPCSClient pcsClient) {
		this.pcsClient = pcsClient;
	}

	public void setPcsPath(String pcsPath) {
		this.pcsPath = pcsPath;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

}
