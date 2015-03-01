package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.yeam.resultmgr.listener.IResultListener;
import com.lezo.iscript.yeam.resultmgr.listener.MessageListener;
import com.lezo.iscript.yeam.resultmgr.listener.RetryListener;
import com.lezo.iscript.yeam.resultmgr.listener.StrategyListener;
import com.lezo.iscript.yeam.resultmgr.listener.WarnListener;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultController implements IResultController {
	private List<IResultListener> listeners;

	public ResultController(List<IResultListener> listeners) {
		super();
		this.listeners = listeners;
	}

	public ResultController() {
		super();
		this.listeners = new ArrayList<IResultListener>();
		this.listeners.add(new RetryListener());
		this.listeners.add(new StrategyListener());
		this.listeners.add(new MessageListener());
		this.listeners.add(new WarnListener());
	}

	@Override
	public void commit(final List<ResultWritable> rWritables) {
		if (CollectionUtils.isEmpty(rWritables)) {
			return;
		}
		for (ResultWritable rw : rWritables) {
			for (IResultListener rsListener : listeners) {
				rsListener.handle(rw);
			}
		}
	}

}
