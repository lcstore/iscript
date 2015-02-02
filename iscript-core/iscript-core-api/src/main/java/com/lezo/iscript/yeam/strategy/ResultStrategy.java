package com.lezo.iscript.yeam.strategy;

import com.lezo.iscript.yeam.writable.ResultWritable;

public interface ResultStrategy {
	public String getName();

	public void handleResult(ResultWritable rWritable);

}
