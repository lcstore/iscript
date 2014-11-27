package com.lezo.iscript.yeam.server.filter;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.lezo.iscript.service.crawler.dto.SessionHisDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.resultmgr.ResultHandlerCaller;
import com.lezo.iscript.yeam.resultmgr.ResultHandlerWoker;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class IoResultFilter extends IoFilterAdapter {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(IoResultFilter.class);

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		handleMessage(session, message);
		super.messageReceived(nextFilter, session, message);
	}

	@SuppressWarnings("unchecked")
	private void handleMessage(IoSession session, Object message) {
		IoRequest ioRequest = (IoRequest) message;
		if (ioRequest == null) {
			return;
		}
		Object rsObject = ioRequest.getData();
		List<ResultWritable> rWritables = (List<ResultWritable>) rsObject;
		if (!CollectionUtils.isEmpty(rWritables)) {
			add2Session(session, rWritables);
			ResultHandlerCaller caller = ResultHandlerCaller.getInstance();
			JSONObject hObject = JSONUtils.getJSONObject(ioRequest.getHeader());
			caller.execute(new ResultHandlerWoker(rWritables));
			ThreadPoolExecutor exec = caller.getExecutor();
			String msg = String.format("add %d result.Client:%s,Result.Caller[active:%d,Largest:%d,Queue:%d]", rWritables.size(), JSONUtils.getString(hObject, "name"), exec.getActiveCount(), exec.getLargestPoolSize(), exec.getQueue().size());
			logger.info(msg);
		}
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		super.messageSent(nextFilter, session, writeRequest);
	}

	private void add2Session(IoSession session, List<ResultWritable> rWritables) {
		int success = 0;
		int fail = 0;
		for (ResultWritable rw : rWritables) {
			if (ResultWritable.RESULT_SUCCESS == rw.getStatus()) {
				success++;
			} else {
				fail++;
			}
		}
		add2Attribute(session, SessionHisDto.SUCCESS_NUM, success);
		add2Attribute(session, SessionHisDto.FAIL_NUM, fail);
	}

	public void add2Attribute(IoSession session, String key, int num) {
		int newValue = (Integer) session.getAttribute(key) + num;
		session.setAttribute(key, newValue);
	}
}
