package com.lezo.iscript.yeam.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.utils.JSONUtils;

public class PersistentWriter implements ObjectWriter<JSONObject> {
	private static final String KEY_SPLIT = "@#@";
	private Logger logger = LoggerFactory.getLogger(PersistentWriter.class);

	@Override
	public void write(List<JSONObject> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		long start = System.currentTimeMillis();
		Map<String, List<String>> key2DataMap = new HashMap<String, List<String>>();
		for (JSONObject gObject : dataList) {
			JSONObject argsObject = JSONUtils.get(gObject, "args");
			String type = JSONUtils.getString(argsObject, "type");
			String batchId = argsObject == null ? null : JSONUtils.getString(argsObject, "bid");
			String dataBucket = argsObject == null ? null : JSONUtils.getString(argsObject, "data_bucket");
			StringBuffer sb = new StringBuffer();
			sb.append(type);
			sb.append(KEY_SPLIT);
			sb.append(getOrDefault(batchId, "-"));
			sb.append(KEY_SPLIT);
			sb.append(getOrDefault(dataBucket, "-"));
			String key = sb.toString();
			List<String> dataListy = key2DataMap.get(key);
			if (dataListy == null) {
				dataListy = new ArrayList<String>();
				key2DataMap.put(key, dataListy);
			}
			dataListy.add(gObject.toString());
		}
		int total = 0;
		PersistentCaller caller = PersistentCaller.getInstance();
		for (Entry<String, List<String>> bEntry : key2DataMap.entrySet()) {
			String[] keyArr = bEntry.getKey().split(KEY_SPLIT);
			int index = -1;
			caller.execute(new PersistentWorker(keyArr[++index], keyArr[++index], keyArr[++index], bEntry.getValue()));
			total++;
		}
		long cost = System.currentTimeMillis() - start;
		logger.info("persistent result.worker:{},Queue:{},cost:{}", total, caller.getExecutor().getQueue().size(), cost);
	}

	private <T> T getOrDefault(T dataBucket, T defaultValue) {
		if (dataBucket == null) {
			return defaultValue;
		}
		return dataBucket;
	}
}
