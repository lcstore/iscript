package com.lezo.iscript.yeam.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.lezo.iscript.yeam.resulter.handle.LoggerResultHandler;
import com.lezo.iscript.yeam.resulter.handle.ResultHandle;
import com.lezo.iscript.yeam.service.ResulterService;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResulterServiceImpl implements ResulterService {

	@Override
	public List<Long> doSubmit(List<ResultWritable> resultList) {
		List<Long> idList = new ArrayList<Long>();
		ResultHandle hander = new LoggerResultHandler();
		if (resultList != null) {
			for (ResultWritable resultWritable : resultList) {
				hander.handle(resultWritable);
				idList.add(resultWritable.getTask().getId());
			}
		}
		return idList;
	}

}
