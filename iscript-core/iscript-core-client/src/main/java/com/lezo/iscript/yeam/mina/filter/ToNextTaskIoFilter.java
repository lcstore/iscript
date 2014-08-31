package com.lezo.iscript.yeam.mina.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ToNextTaskIoFilter extends IoFilterAdapter {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ToNextTaskIoFilter.class);

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		Object msg = writeRequest.getMessage();
		if (msg instanceof IoRequest) {
			IoRequest request = (IoRequest) msg;
			if (IoConstant.EVENT_TYPE_RESULT == request.getType()) {
				List<ResultWritable> successList = new ArrayList<ResultWritable>();
				List<ResultWritable> errorList = new ArrayList<ResultWritable>();
				// assort into success & error
				doAssort(request, successList, errorList);
				List<ResultWritable> hasNextTaskList = new ArrayList<ResultWritable>();
				List<ResultWritable> otherList = new ArrayList<ResultWritable>();
				// get has next task
				convertToNextTask(successList, hasNextTaskList, otherList);
				List<ResultWritable> toSendList = new ArrayList<ResultWritable>(errorList);
				toSendList.addAll(hasNextTaskList);
				toSendList.addAll(otherList);
				request.setData(toSendList);
				logger.info(String.format("Result,success:%s,error:%s,hasNext:%s,send:%s", successList.size(),
						errorList.size(), hasNextTaskList.size(), toSendList.size()));
			}
		}
		super.messageSent(nextFilter, session, writeRequest);
	}

	private void convertToNextTask(List<ResultWritable> successList, List<ResultWritable> hasNextTaskList,
			List<ResultWritable> otherList) {
		for (ResultWritable rw : successList) {
			JSONObject gObject = JSONUtils.getJSONObject(rw.getResult());
			JSONObject rsObject = JSONUtils.get(gObject, "rs");
			if (rsObject == null) {
				otherList.add(rw);
			} else if (rsObject.has("nexts")) {
				JSONArray nextArray = JSONUtils.get(rsObject, "nexts");
				if (nextArray.length() > 0) {
					JSONObject newObject = new JSONObject();
					JSONUtils.put(newObject, "nexts", nextArray);
					JSONUtils.put(gObject, "rs", newObject);
					rw.setResult(gObject.toString());
					hasNextTaskList.add(rw);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void doAssort(IoRequest request, List<ResultWritable> successList, List<ResultWritable> errorList) {
		List<ResultWritable> rsList = (List<ResultWritable>) request.getData();
		for (ResultWritable rs : rsList) {
			if (ResultWritable.RESULT_SUCCESS == rs.getStatus()) {
				successList.add(rs);
			} else {
				errorList.add(rs);
			}
		}
	}

}
