package com.lezo.iscript.yeam.mina.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ToBatchIoFilter extends IoFilterAdapter {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ToBatchIoFilter.class);

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		Object msg = writeRequest.getMessage();
		if (msg instanceof IoRequest) {
			IoRequest request = (IoRequest) msg;
			if (IoConstant.EVENT_TYPE_RESULT == request.getType()) {
				@SuppressWarnings("unchecked")
				List<ResultWritable> rsList = (List<ResultWritable>) request.getData();
				BatchIterator<ResultWritable> it = new BatchIterator<ResultWritable>(rsList, 20);
				while (it.hasNext()) {
					List<ResultWritable> blockList = new ArrayList<ResultWritable>(it.next());
					request.setData(blockList);
					super.messageSent(nextFilter, session, writeRequest);
				}
			}
		} else {
			super.messageSent(nextFilter, session, writeRequest);
		}
	}
}
