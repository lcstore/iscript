package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class TmallBrandShopStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(TmallBrandShopStrategy.class);
	private static volatile boolean running = false;
	private Timer timer;

	public TmallBrandShopStrategy() {
		CreateTaskTimer task = new CreateTaskTimer();
		this.timer = new Timer("CreateTaskTimer");
		this.timer.schedule(task, 60 * 1000, 24 * 60 * 60 * 1000);
	}

	private class CreateTaskTimer extends TimerTask {
		private Map<String, Set<String>> typeMap;

		public CreateTaskTimer() {
			typeMap = new HashMap<String, Set<String>>();
			Set<String> urlSet = new HashSet<String>();
			urlSet.add("http://brand.tmall.com/brandMap.htm?spm=a3200.2192449.0.0.6OiqFL");
			for (int i = 65; i <= 90; i++) {
				char word = (char) i;
				urlSet.add("http://brand.tmall.com/azIndexInside.htm?firstLetter=" + word);
			}
			typeMap.put("ConfigTmallBrandList", urlSet);
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=100&categoryId=50025135&etgId=59");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=100&categoryId=50025174&etgId=58");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=100&categoryId=50023887&etgId=60");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=100&categoryId=50025983&etgId=61");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=50025829&etgId=64");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=50026637&etgId=63");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=51052003&etgId=65");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=51042006&etgId=66");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=50072916&etgId=188");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=50095658&etgId=68");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=111&categoryId=50108176&etgId=190");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=111&categoryId=50026474&etgId=74");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=111&categoryId=50026478&etgId=78");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=111&categoryId=50026461&etgId=80");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=111&categoryId=50023064&etgId=82");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026502&etgId=70");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026391&etgId=69");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026506&etgId=73");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026505&etgId=71");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026426&etgId=72");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026393&etgId=187");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50043479&etgId=138");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=110&categoryId=50020894&etgId=83");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=110&categoryId=50020909&etgId=84");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=110&categoryId=50043669&etgId=194");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=110&categoryId=50022787&etgId=195");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024400&etgId=99");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024399&etgId=100");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024401&etgId=101");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50047403&etgId=103");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50047396&etgId=110");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024407&etgId=102");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024406&etgId=104");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50043917&etgId=105");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50099232&etgId=106");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024410&etgId=107");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50094901&etgId=108");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024411&etgId=109");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=103&categoryId=50900004&etgId=94");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=103&categoryId=50892008&etgId=95");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=103&categoryId=50902003&etgId=96");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=103&categoryId=50886005&etgId=97");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=103&categoryId=50894004&etgId=98");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030787&etgId=111");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50067162&etgId=112");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50067174&etgId=113");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50051691&etgId=114");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50097362&etgId=115");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030207&etgId=116");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030215&etgId=117");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030223&etgId=118");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030221&etgId=119");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030212&etgId=120");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030213&etgId=121");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030204&etgId=122");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030220&etgId=124");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030203&etgId=125");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50069204&etgId=126");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50069234&etgId=127");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50067917&etgId=128");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50024531&etgId=129");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50068087&etgId=130");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50072436&etgId=133");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50068090&etgId=131");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50036568&etgId=134");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50067939&etgId=132");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50036640&etgId=135");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50034368&etgId=136");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50072285&etgId=137");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100151&etgId=174");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50072046&etgId=168");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50072044&etgId=169");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100152&etgId=175");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100153&etgId=176");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100154&etgId=177");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50099890&etgId=178");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50074901&etgId=171");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50099887&etgId=179");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100167&etgId=180");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100166&etgId=181");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50099298&etgId=182");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50074804&etgId=170");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50074917&etgId=172");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50074933&etgId=173");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50025137&etgId=139");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50023647&etgId=148");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50029253&etgId=152");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50036697&etgId=147");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50024803&etgId=191");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50033500&etgId=193");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50106135&etgId=155");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50029838&etgId=157");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50029836&etgId=158");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50029852&etgId=159");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50029840&etgId=162");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50044102&etgId=163");
		}

		@Override
		public void run() {
			if (running) {
				logger.warn("CreateTaskTimer is working...");
				return;
			}
			long start = System.currentTimeMillis();
			try {
				logger.info("CreateTaskTimer is start...");
				running = true;
				JSONObject argsObject = new JSONObject();
				JSONUtils.put(argsObject, "strategy", getName());
				for (Entry<String, Set<String>> entry : typeMap.entrySet()) {
					List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
					String taskId = UUID.randomUUID().toString();
					JSONUtils.put(argsObject, "bid", taskId);
					JSONUtils.put(argsObject, "retry", 0);
					String type = entry.getKey();
					for (String url : entry.getValue()) {
						TaskPriorityDto taskDto = createPriorityDto(url, type, argsObject);
						taskList.add(taskDto);
					}
					getTaskPriorityDtoBuffer().addAll(taskList);
					logger.info("Offer task:{},size:{}", type, taskList.size());
				}
			} catch (Exception ex) {
				logger.warn(ExceptionUtils.getStackTrace(ex));
			} finally {
				long cost = System.currentTimeMillis() - start;
				logger.info("CreateTaskTimer is done.cost:{}", cost);
				running = false;
			}
		}
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {
		if (ResultWritable.RESULT_SUCCESS != rWritable.getStatus()) {
			return;
		}
		if (rWritable.getType().indexOf("BrandList") > 0) {
			JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
			JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
			JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
			try {
				argsObject.remove("name@client");
				argsObject.remove("target");
				addOthers(rWritable, rsObject, argsObject);
				addNextTasks(rsObject, argsObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void addOthers(ResultWritable rWritable, JSONObject rsObject, JSONObject argsObject) throws JSONException {
		JSONArray dataArray = JSONUtils.get(rsObject, "dataList");
		if (dataArray == null) {
			return;
		}
		int len = dataArray.length();
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>(len * 2);
		JSONObject oParamObject = JSONUtils.getJSONObject(argsObject.toString());
		String url = JSONUtils.getString(argsObject, "url");
		JSONUtils.put(oParamObject, "fromUrl", url);
		String productType = rWritable.getType().replace("BrandList", "BrandShop");
		String argsString = oParamObject.toString();
		for (int i = 0; i < len; i++) {
			JSONObject bObject = dataArray.getJSONObject(i);
			JSONObject paramObject = new JSONObject(argsString);
			String brandUrl = JSONUtils.getString(bObject, "brandUrl");
			bObject.remove("brandUrl");
			Iterator<?> it = bObject.keys();
			while (it.hasNext()) {
				String key = it.next().toString();
				JSONUtils.put(paramObject, key, JSONUtils.get(bObject, key));
			}
			TaskPriorityDto taskPriorityDto = createPriorityDto(brandUrl, productType, paramObject);
			dtoList.add(taskPriorityDto);
		}
		getTaskPriorityDtoBuffer().addAll(dtoList);

	}

	private void addNextTasks(JSONObject rsObject, JSONObject argsObject) throws Exception {
		JSONArray nextArray = JSONUtils.get(rsObject, "nextList");
		if (nextArray == null) {
			return;
		}
		String type = JSONUtils.getString(argsObject, "type");
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
		JSONUtils.put(argsObject, "fromUrl", JSONUtils.getString(argsObject, "url"));
		for (int i = 0; i < nextArray.length(); i++) {
			String nextUrl = nextArray.getString(i);
			TaskPriorityDto taskPriorityDto = createPriorityDto(nextUrl, type, argsObject);
			dtoList.add(taskPriorityDto);
		}
		getTaskPriorityDtoBuffer().addAll(dtoList);
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
		taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
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

	private StorageBuffer<TaskPriorityDto> getTaskPriorityDtoBuffer() {
		return (StorageBuffer<TaskPriorityDto>) StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class);
	}

	@Override
	public void close() throws IOException {
		if (this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}
		logger.info("close " + getName() + " strategy..");
	}
}