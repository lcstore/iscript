package com.lezo.iscript.yeam.resultmgr.listener;

import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.CrawlerWarnHisDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class WarnListener implements IResultListener {
	private static Logger logger = LoggerFactory.getLogger(WarnListener.class);

	@Override
	public void handle(ResultWritable result) {
		if (ResultWritable.RESULT_SUCCESS == result.getStatus()) {
			return;
		}
		JSONObject gObject = JSONUtils.getJSONObject(result.getResult());
		JSONObject argsObject = JSONUtils.get(gObject, "args");
		JSONObject exObject = JSONUtils.get(gObject, "ex");
		logger.warn(String.format("type:%s,taskId:%s,args:%s,cause:%s.", result.getType(), result.getTaskId(), argsObject, exObject));
		Integer retry = JSONUtils.getInteger(argsObject, "retry");
		retry = retry == null ? 0 : retry;
		String clienName = JSONUtils.getString(argsObject, "name@client");
		CrawlerWarnHisDto dto = new CrawlerWarnHisDto();
		dto.setType(result.getType());
		dto.setClienName(clienName);
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());
		dto.setTaskId(result.getTaskId());
		dto.setProcessId(JSONUtils.getString(argsObject, "bid"));
		dto.setRetry(retry);
		dto.setMessage(exObject.toString());
		argsObject.remove("name@client");
		argsObject.remove("bid");
		argsObject.remove("type");
	}
}
