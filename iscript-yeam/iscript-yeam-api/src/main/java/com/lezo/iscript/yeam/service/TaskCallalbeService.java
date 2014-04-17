package com.lezo.iscript.yeam.service;

import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public interface TaskCallalbeService {
	ResultWritable doCall(TaskWritable task);
}
