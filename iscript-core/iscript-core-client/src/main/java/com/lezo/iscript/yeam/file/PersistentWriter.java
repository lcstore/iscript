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
	private Logger logger = LoggerFactory.getLogger(PersistentWriter.class);

	@Override
	public void write(List<JSONObject> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		long start = System.currentTimeMillis();
		Map<String, Map<String, List<String>>> typeMap = new HashMap<String, Map<String, List<String>>>();
		for (JSONObject gObject : dataList) {
			JSONObject argsObject = JSONUtils.get(gObject, "args");
			String type = JSONUtils.getString(argsObject, "type");
			Map<String, List<String>> batchMap = typeMap.get(type);
			if (batchMap == null) {
				batchMap = new HashMap<String, List<String>>();
				typeMap.put(type, batchMap);
			}
			String batchId = argsObject == null ? null : JSONUtils.getString(argsObject, "bid");
			batchId = batchId == null ? "-" : batchId;
			List<String> resultList = batchMap.get(batchId);
			if (resultList == null) {
				resultList = new ArrayList<String>();
				batchMap.put(batchId, resultList);
			}
			resultList.add(gObject.toString());
		}
		int total = 0;
		PersistentCaller caller = PersistentCaller.getInstance();
		for (Entry<String, Map<String, List<String>>> entry : typeMap.entrySet()) {
			for (Entry<String, List<String>> bEntry : entry.getValue().entrySet()) {
				caller.execute(new PersistentWorker(entry.getKey(), bEntry.getKey(), bEntry.getValue()));
				total++;
			}
		}
		long cost = System.currentTimeMillis() - start;
		logger.info("persistent result.worker:{},Queue:{},cost:{}", total, caller.getExecutor().getQueue().size(), cost);
	}

	@Override
	public void flush() {

	}

}
