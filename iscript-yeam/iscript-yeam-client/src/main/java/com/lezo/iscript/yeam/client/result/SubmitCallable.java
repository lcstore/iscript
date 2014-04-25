package com.lezo.iscript.yeam.client.result;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.client.utils.ClientRemoteUtils;
import com.lezo.iscript.yeam.service.ResulterService;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class SubmitCallable implements Callable<List<Long>> {
	private Logger log = Logger.getLogger(SubmitCallable.class);
	private String resulterHost;
	private List<ResultWritable> resultList;

	public SubmitCallable(String resulterHost, List<ResultWritable> resultList) {
		super();
		this.resulterHost = resulterHost;
		this.resultList = resultList;
	}

	@Override
	public List<Long> call() throws Exception {
		try {
			if (CollectionUtils.isEmpty(resultList)) {
				return Collections.emptyList();
			}
			ResulterService resulterService = ClientRemoteUtils.getResulterService(resulterHost);
			return resulterService.doSubmit(resultList);
		} catch (Exception e) {
			log.warn("", e);
			throw e;
		}
	}
}
