package com.lezo.iscript.yeam.resulter;

import java.util.List;

import com.lezo.iscript.yeam.writable.ResultWritable;

public interface Resultable {
	void doCall(List<ResultWritable> resultList);
}
