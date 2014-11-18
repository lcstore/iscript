package com.lezo.iscript.yeam.server.event;

import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;

public class HeadAnalyzer implements Runnable {
	private String header;
	private IoSession ioSession;

	public HeadAnalyzer(String header, IoSession ioSession) {
		super();
		this.header = header;
		this.ioSession = ioSession;
	}

	@Override
	public void run() {
		JSONObject hObject = JSONUtils.getJSONObject(header);

	}

}
