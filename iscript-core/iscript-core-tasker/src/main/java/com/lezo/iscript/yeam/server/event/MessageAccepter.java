package com.lezo.iscript.yeam.server.event;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;

public class MessageAccepter implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(MessageAccepter.class);
	private IoRequest ioRequest;
	private IoSession ioSession;

	public MessageAccepter(IoRequest ioRequest, IoSession ioSession) {
		super();
		this.ioRequest = ioRequest;
		this.ioSession = ioSession;
	}

	@Override
	public void run() {
		if (ioRequest.getType() == IoConstant.EVENT_TYPE_RESULT) {
			DataProceser.getInstance().execute(new DataAnalyzer(ioRequest, ioSession));
		}
		String header = ioRequest.getHeader();
		if (StringUtils.isEmpty(header)) {
			HeadProceser.getInstance().execute(new HeadAnalyzer(header, ioSession));
		} else {
			logger.warn("empty header.session:" + ioSession.getId() + ",addr:" + ioSession.getRemoteAddress());
		}
	}

}
