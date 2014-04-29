package com.lezo.iscript.yeam.client.service.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.service.TaskCallalbeService;
import com.lezo.iscript.yeam.service.impl.TaskCallalbeServiceImpl;
import com.lezo.iscript.yeam.writable.ConfigWritable;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TestTaskCallalbeServiceImpl {

	@Test
	public void testCallable() {
		TaskCallalbeService taskCallalbeService = new TaskCallalbeServiceImpl();
		TaskWritable task = new TaskWritable();
		String type = "sum.xml";
		task.setId(1L);
		task.put("type", type);
		task.put("x", 1);
		task.put("y", 2);
		ConfigWritable configWritable = new ConfigWritable();
		configWritable.setName(type);
		String content = "return args.x + args.y;";
		configWritable.setContent(content.getBytes());
		configWritable.setStamp(System.currentTimeMillis());
		ConfigParserBuffer.getInstance().addConfig(type, configWritable);
		ResultWritable rs = taskCallalbeService.doCall(task);
		Assert.assertEquals(3, Integer.valueOf(rs.getResult()).intValue());
	}

	@Test
	public void testCallableJson() throws JSONException {
		TaskCallalbeService taskCallalbeService = new TaskCallalbeServiceImpl();
		String type = "jsonrs.xml";
		TaskWritable task = new TaskWritable();
		task.setId(1L);
		task.put("type", type);
		task.put("x", 1);
		task.put("y", 2);
		ConfigWritable configWritable = new ConfigWritable();
		configWritable.setName(type);
		String content = "var rs = {}; rs.x = args.x;rs.y=args.y; rs.sum = rs.x + rs.y; return JSON.stringify(rs);";
		configWritable.setContent(content.getBytes());
		configWritable.setStamp(System.currentTimeMillis());
		ConfigParserBuffer.getInstance().addConfig(type, configWritable);
		ResultWritable rs = taskCallalbeService.doCall(task);
		JSONObject jsonObject = new JSONObject(rs.getResult());
		System.out.println(rs.getResult());
		Assert.assertEquals(3, jsonObject.getInt("sum"));
	}
}
