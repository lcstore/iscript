package com.lezo.iscript.yeam.service;

import java.util.List;

import com.lezo.iscript.yeam.writable.ResultWritable;

public interface ResulterService {
	List<Long> doSubmit(List<ResultWritable> resultList);
}
