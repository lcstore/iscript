package com.lezo.iscript.service.crawler.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;

import com.lezo.iscript.service.crawler.dao.TaskPriorityDao;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;

public class TaskPriorityDaoImpl implements TaskPriorityDao {
	private SqlSession sqlSessionTemplate;

	public void setSqlSessionTemplate(SqlSession sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Override
	public void batchInsert(List<TaskPriorityDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		sqlSessionTemplate.insert("batchInsert-TaskPriorityDto", dtoList);
	}

	@Override
	public int batchUpdate(List<Long> taskIds, int status) {
		if (CollectionUtils.isEmpty(taskIds)) {
			return 0;
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("taskIds", taskIds);
		param.put("status", status);
		return sqlSessionTemplate.update("update-tasks-status", param);
	}

	@Override
	public List<TaskPriorityDto> getTaskPriorityDtos(String type, int level, int status, int limit) {
		if (StringUtils.isEmpty(type)) {
			return java.util.Collections.emptyList();
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("type", type);
		param.put("level", level);
		param.put("status", status);
		param.put("limit", limit);
		return sqlSessionTemplate.selectList("select-by-type_level_status_limit", param);
	}

	@Override
	public List<TaskPriorityDto> getTaskTypeLevels(List<String> typeList, int status) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("typeList", typeList);
		param.put("status", status);
		return sqlSessionTemplate.selectList("select-typeLevel-by-status", param);
	}

}
