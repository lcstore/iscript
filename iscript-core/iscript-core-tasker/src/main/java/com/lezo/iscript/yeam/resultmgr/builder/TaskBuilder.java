package com.lezo.iscript.yeam.resultmgr.builder;

import java.util.List;

import org.json.JSONObject;

import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;

public interface TaskBuilder {
	List<TaskPriorityDto> buildTasks(JSONObject gObject);
}
