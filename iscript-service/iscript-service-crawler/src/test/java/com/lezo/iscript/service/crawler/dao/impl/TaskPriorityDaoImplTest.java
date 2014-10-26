package com.lezo.iscript.service.crawler.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.TaskPriorityDao;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;

public class TaskPriorityDaoImplTest {

	@Test
	public void test() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		TaskPriorityDao taskPriorityDao = SpringBeanUtils.getBean(TaskPriorityDao.class);
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
		TaskPriorityDto e = new TaskPriorityDto();
		e.setBatchId("bid");
		e.setLevel(0);
		e.setSource("test");
		e.setStatus(0);
		e.setType("test-type");
		dtoList.add(e);
		taskPriorityDao.batchInsert(dtoList);
	}

	@Test
	public void testBarCode() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		TaskPriorityDao taskPriorityDao = SpringBeanUtils.getBean(TaskPriorityDao.class);
		List<String> lines = FileUtils.readLines(new File("E:/lezo/codes/barCode437751.log"), "UTF-8");
		String type = "ConfigBarCodeMatcher";
		JSONObject argsObject = new JSONObject();
		int total = 0;
		JSONUtils.put(argsObject, "src", "create");
		JSONUtils.put(argsObject, "level", 100);
		JSONUtils.put(argsObject, "strategy", "BarCodeMatchStrategy");
		JSONUtils.put(argsObject, "retry", 0);
		for (String line : lines) {
			List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
			JSONArray dArray = new JSONArray(line);
			int len = dArray.length();
			JSONUtils.put(argsObject, "bid", "bc" + total);
			for (int i = 0; i < len; i++) {
				String barCode = dArray.getString(i);
				JSONUtils.put(argsObject, "barCode", barCode);
				TaskPriorityDto dto = createPriorityDto("", type, argsObject);
				dtoList.add(dto);
			}
			taskPriorityDao.batchInsert(dtoList);
			total += dtoList.size();
			System.err.println("total:"+total);
		}
		System.err.println("total:"+total);
	}

	private TaskPriorityDto createPriorityDto(String url, String type, JSONObject argsObject) {
		String taskId = JSONUtils.getString(argsObject, "bid");
		taskId = taskId == null ? UUID.randomUUID().toString() : taskId;
		TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
		taskPriorityDto.setBatchId(taskId);
		taskPriorityDto.setType(type);
		taskPriorityDto.setUrl(url);
		taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
		taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
		taskPriorityDto.setCreatTime(new Date());
		taskPriorityDto.setUpdateTime(taskPriorityDto.getCreatTime());
		taskPriorityDto.setStatus(0);
		JSONObject paramObject = JSONUtils.getJSONObject(argsObject.toString());
		paramObject.remove("bid");
		paramObject.remove("type");
		paramObject.remove("url");
		paramObject.remove("level");
		paramObject.remove("src");
		paramObject.remove("ctime");
		if (taskPriorityDto.getLevel() == null) {
			taskPriorityDto.setLevel(0);
		}
		taskPriorityDto.setParams(paramObject.toString());
		return taskPriorityDto;
	}
}
