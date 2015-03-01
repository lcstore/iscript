package com.lezo.iscript.yeam.resultmgr;

import java.util.List;

import com.lezo.iscript.yeam.writable.ResultWritable;

public interface IResultController {
	void commit(final List<ResultWritable> rWritables);
}
