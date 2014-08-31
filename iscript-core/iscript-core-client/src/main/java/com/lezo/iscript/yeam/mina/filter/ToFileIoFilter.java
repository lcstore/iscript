package com.lezo.iscript.yeam.mina.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.result.PersistentCaller;
import com.lezo.iscript.yeam.result.PersistentWorker;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ToFileIoFilter extends IoFilterAdapter {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ToFileIoFilter.class);

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		Object msg = writeRequest.getMessage();
		if (msg instanceof IoRequest) {
			IoRequest request = (IoRequest) msg;
			if (IoConstant.EVENT_TYPE_RESULT == request.getType()) {
				List<ResultWritable> successList = new ArrayList<ResultWritable>();
				doAssort(request, successList);
				convertToFile(successList);
			}
		}
		super.messageSent(nextFilter, session, writeRequest);
	}

	@SuppressWarnings("unchecked")
	private void doAssort(IoRequest request, List<ResultWritable> successList) {
		List<ResultWritable> rsList = (List<ResultWritable>) request.getData();
		for (ResultWritable rs : rsList) {
			if (ResultWritable.RESULT_SUCCESS == rs.getStatus()) {
				successList.add(rs);
			}
		}
	}

	private void convertToFile(List<ResultWritable> successList) {
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
			String batchId = JSONUtils.getString(argsObject, "bid");
			batchId = batchId == null ? "" : batchId;
			List<ResultWritable> resultList = batchMap.get(batchId);
			if (resultList == null) {
				resultList = new ArrayList<ResultWritable>();
				batchMap.put(batchId, resultList);
				total++;
			}
			resultList.add(rw);
		}
		PersistentCaller caller = PersistentCaller.getInstance();
		for (Entry<String, Map<String, List<ResultWritable>>> entry : typeMap.entrySet()) {
			for (Entry<String, List<ResultWritable>> bEntry : entry.getValue().entrySet()) {
				caller.execute(new PersistentWorker(entry.getKey(), bEntry.getKey(), bEntry.getValue()));
			}
		}
		logger.info(String.format("convert result:%s,dest file:%s", successList.size(), total));
	}

}
