package com.lezo.iscript.yeam.client.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultsHolder {
	private Logger log = Logger.getLogger(ResultsHolder.class);
	private final ConcurrentHashMap<String, List<ResultWritable>> resultListMap = new ConcurrentHashMap<String, List<ResultWritable>>();

	private static class InstanceHolder {
		private static final ResultsHolder instance = new ResultsHolder();
	}

	public static ResultsHolder getInstance() {
		return InstanceHolder.instance;
	}

	public void addResult(String resulter, ResultWritable resultWritable) {
		if (resulter == null) {
			log.warn("add result to empty result host.taskId:" + resultWritable.getTaskId() + ",result:"
					+ resultWritable.getResult());
			return;
		}
		List<ResultWritable> rsList = resultListMap.get(resulter);
		if (rsList == null) {
			rsList = new ArrayList<ResultWritable>();
			resultListMap.putIfAbsent(resulter, rsList);
		}
		rsList.add(resultWritable);
	}

	public Iterator<Entry<String, List<ResultWritable>>> iterator() {
		List<Entry<String, List<ResultWritable>>> entries = new ArrayList<Map.Entry<String, List<ResultWritable>>>(
				resultListMap.size());
		for (Entry<String, List<ResultWritable>> entry : resultListMap.entrySet()) {
			entries.add(entry);
		}
		return entries.iterator();
	}

	public void clear() {
		resultListMap.clear();
	}

}
