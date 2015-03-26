package com.lezo.iscript.yeam.mina.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.mina.utils.ServerTimeUtils;

public class TimeIoFilter extends IoFilterAdapter {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(TimeIoFilter.class);
	private static final Long CONNECT_COST_MILLS = 5L;
    
	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		IoRespone ioRespone = (IoRespone) message;
		ServerTimeUtils.setTargetMills(ioRespone.getTimeMills(),CONNECT_COST_MILLS);
		nextFilter.messageReceived(session, message);
	}

}
