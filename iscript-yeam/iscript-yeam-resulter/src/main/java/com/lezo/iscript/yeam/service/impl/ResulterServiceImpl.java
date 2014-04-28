package com.lezo.iscript.yeam.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.lezo.iscript.yeam.resulter.Resultable;
import com.lezo.iscript.yeam.service.ResulterService;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResulterServiceImpl implements ResulterService {
	private Resultable resultable;

	@Override
	public List<Long> doSubmit(List<ResultWritable> resultList) {
		List<Long> idList = new ArrayList<Long>();
		for (ResultWritable resultWritable : resultList) {
			idList.add(resultWritable.getTaskId());
		}
		resultable.doCall(resultList);
		return idList;
	}

	public void setResultable(Resultable resultable) {
		this.resultable = resultable;
	}

}
