package com.lezo.iscript.yeam.server.session;

import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.server.event.HeadAnalyzer;

public abstract class AbstractActionHandler implements ActionHandler {
	private static Logger logger = LoggerFactory.getLogger(HeadAnalyzer.class);

	@Override
	public boolean isFilter(JSONObject hObject) {
		return false;
	}

	@Override
	public void callAction(JSONObject hObject, IoSession ioSession) {
		if (isFilter(hObject)) {
			logger.info("the action is filter.head:" + hObject);
		} else {
			doAction(hObject, ioSession);
			doRecord(hObject);
		}
	}

	protected abstract Object doAction(JSONObject hObject, IoSession ioSession);

	private void doRecord(JSONObject hObject) {
		String clientName = JSONUtils.getString(hObject, "name");
		ActionRecord record = ActionRecorder.getInstance().getRecord(clientName);
		Integer tactive = JSONUtils.getInteger(hObject, "tactive");
		Integer tsize = JSONUtils.getInteger(hObject, "tsize");
		StringBuilder sb = new StringBuilder();
		sb.append(tactive);
		sb.append(".");
		sb.append(tsize);
		String checkValue = sb.toString();
		record.doRecord(checkValue);
	}
}
