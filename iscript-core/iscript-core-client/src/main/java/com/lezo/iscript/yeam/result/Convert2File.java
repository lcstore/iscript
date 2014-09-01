package com.lezo.iscript.yeam.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class Convert2File implements Convertable<List<PersistentWorker>> {

	public List<PersistentWorker> doConvert(List<ResultWritable> rsList) {
		List<ResultWritable> successList = new ArrayList<ResultWritable>();
		doAssort(rsList, successList);
		return convertToFile(successList);
	}

	private void doAssort(List<ResultWritable> rsList, List<ResultWritable> successList) {
		for (ResultWritable rs : rsList) {
			if (ResultWritable.RESULT_SUCCESS == rs.getStatus()) {
				successList.add(rs);
			}
		}
	}

	private List<PersistentWorker> convertToFile(List<ResultWritable> successList) {
		Map<String, Map<String, List<ResultWritable>>> typeMap = new HashMap<String, Map<String, List<ResultWritable>>>();
		int total = 0;
		for (ResultWritable rw : successList) {
			Map<String, List<ResultWritable>> batchMap = typeMap.get(rw.getType());
			if (batchMap == null) {
				batchMap = new HashMap<String, List<ResultWritable>>();
				typeMap.put(rw.getType(), batchMap);
			}
			JSONObject gObject = JSONUtils.getJSONObject(rw.getResult());
			JSONObject argsObject = JSONUtils.get(gObject, "args");
			String batchId = argsObject == null ? null : JSONUtils.getString(argsObject, "bid");
			batchId = batchId == null ? "-" : batchId;
			List<ResultWritable> resultList = batchMap.get(batchId);
			if (resultList == null) {
				resultList = new ArrayList<ResultWritable>();
				batchMap.put(batchId, resultList);
				total++;
			}
			resultList.add(rw);
		}
		List<PersistentWorker> workers = new ArrayList<PersistentWorker>(total);
		for (Entry<String, Map<String, List<ResultWritable>>> entry : typeMap.entrySet()) {
			for (Entry<String, List<ResultWritable>> bEntry : entry.getValue().entrySet()) {
				workers.add(new PersistentWorker(entry.getKey(), bEntry.getKey(), bEntry.getValue()));
			}
		}
		return workers;
	}
}
