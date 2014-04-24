package com.lezo.iscript.yeam.resulter.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.pcs.BaiduPCSClient;
import com.lezo.iscript.yeam.ResultConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class PcsResultHandler implements ResultHandle {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private BaiduPCSClient pcsClient;
	private String pcsPath;

	@Override
	public void handle(ResultWritable resultWritable) {
		if (resultWritable.getStatus() == ResultConstant.RESULT_SUCCESS) {

		}
		log.info("task args:" + resultWritable.getTask().getArgs() + ",rs:" + resultWritable.getResult()
				+ ",result args:" + resultWritable.getArgs());

	}

	public void setPcsClient(BaiduPCSClient pcsClient) {
		this.pcsClient = pcsClient;
	}

	public void setPcsPath(String pcsPath) {
		this.pcsPath = pcsPath;
	}

}
