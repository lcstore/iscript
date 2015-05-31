package com.lezo.iscript.yeam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.ConfigTmallBrandShop;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class LocalClientTest {
	private static Logger logger = LoggerFactory.getLogger(LocalClientTest.class);

	public static void main(String[] args) throws Exception {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
		List<String> lineList = FileUtils.readLines(new File("src/test/resources/data/tm.brandId.work.txt"), "UTF-8");
		String taskId = UUID.randomUUID().toString();
		String type = "ConfigTmallBrandShop";
		Queue<Future<ResultWritable>> futureQueue = new LinkedBlockingQueue<Future<ResultWritable>>();
		Long index = 0L;
		for (String brandId : lineList) {
			String url = "http://list.tmall.com/search_product.htm?brand=" + brandId + "&sort=s&style=w";
			final TaskWritable task = new TaskWritable();
			task.put("url", url);
			task.put("type", type);
			task.put("bid", taskId);
			task.put("retry", 0);
			task.setId(++index);
			Future<ResultWritable> future = executor.submit(new CallTask(task));
			futureQueue.add(future);
		}
		Writer out = new FileWriter(new File("src/test/resources/data/tm.brand.result." + System.currentTimeMillis()
				+ ".txt"), true);
		BufferedWriter bw = new BufferedWriter(out);
		long timeout = 10;
		while (!futureQueue.isEmpty() || executor.getActiveCount() > 0 || !executor.getQueue().isEmpty()) {
			logger.info("future:" + futureQueue.size() + ",sleep:" + timeout);
			TimeUnit.SECONDS.sleep(timeout);
			Iterator<Future<ResultWritable>> it = futureQueue.iterator();
			while (it.hasNext()) {
				Future<ResultWritable> future = it.next();
				if (future.isDone()) {
					ResultWritable resultWritable = future.get();
					JSONObject returnObject = JSONUtils.getJSONObject(resultWritable.getResult());
					JSONObject argsObject = JSONUtils.getJSONObject(returnObject, "args");
					if (resultWritable.getStatus() == ResultWritable.RESULT_FAIL) {
						logger.warn("fail:" + returnObject);
						Integer retry = JSONUtils.getInteger(argsObject, "retry");
						if (retry != null && retry < 3) {
							final TaskWritable task = new TaskWritable();
							task.put("url", JSONUtils.getString(argsObject, "url"));
							task.put("type", JSONUtils.getString(argsObject, "type"));
							task.put("bid", JSONUtils.getString(argsObject, "bid"));
							task.put("retry", retry + 1);
							task.setId(resultWritable.getTaskId());
							Future<ResultWritable> retryFuture = executor.submit(new CallTask(task));
							futureQueue.add(retryFuture);
						}
					} else {
						JSONObject storageDataObject = JSONUtils.getJSONObject(returnObject,
								ClientConstant.KEY_STORAGE_RESULT);
						if (storageDataObject != null) {
							JSONObject storageObject = new JSONObject();
							JSONUtils.put(storageObject, "args", argsObject);
							JSONUtils.put(storageObject, "rs", storageDataObject);
							bw.append(storageObject.toString());
							bw.append("\n");
						}
						JSONObject callBackDataObject = JSONUtils.getJSONObject(returnObject,
								ClientConstant.KEY_CALLBACK_RESULT);
						if (callBackDataObject != null && callBackDataObject.has("nextList")) {
							JSONArray nextList = JSONUtils.get(callBackDataObject, "nextList");
							for (int i = 0; i < nextList.length(); i++) {
								JSONUtils.put(argsObject, "url", nextList.get(i));
								final TaskWritable task = new TaskWritable();
								task.setId(++index);
								Iterator<?> argsIt = argsObject.keys();
								while (argsIt.hasNext()) {
									String key = argsIt.next().toString();
									task.put(key, JSONUtils.get(argsObject, key));
									Future<ResultWritable> nextFuture = executor.submit(new CallTask(task));
									futureQueue.add(nextFuture);
								}
							}
							logger.info("offer next:" + nextList.length());
						}
					}
					it.remove();
				}
			}
		}
		bw.flush();
		IOUtils.closeQuietly(bw);
		executor.shutdownNow();
		logger.info("finish to handle task..");
	}

	private static class CallTask implements Callable<ResultWritable> {

		private TaskWritable task;

		public CallTask(TaskWritable task) {
			super();
			this.task = task;
		}

		@Override
		public ResultWritable call() throws Exception {
			long start = System.currentTimeMillis();
			ResultWritable rsWritable = new ResultWritable();
			rsWritable.setTaskId(task.getId());
			JSONObject callBackObject = new JSONObject();
			JSONObject argsObject = new JSONObject(task.getArgs());
			String type = null;
			try {
				Object typeObject = task.get("type");
				if (typeObject == null) {
					throw new IllegalArgumentException("No type for id:" + task.getId() + ",args:" + argsObject);
				}
				type = typeObject.toString();
				rsWritable.setType(type);
				// argsObject.remove("type");
				ConfigParser parser = new ConfigTmallBrandShop();
				String sReturn = parser.doParse(task);
				rsWritable.setStatus(ResultWritable.RESULT_SUCCESS);
				callBackObject = JSONUtils.getJSONObject(sReturn);
			} catch (Exception e) {
				rsWritable.setStatus(ResultWritable.RESULT_FAIL);
				String strStack = ExceptionUtils.getStackTrace(e);
				JSONObject exObject = new JSONObject();
				JSONUtils.put(exObject, "name", e.getClass().getName());
				JSONUtils.put(exObject, "stack", ExceptionUtils.getStackTrace(e));
				JSONUtils.put(callBackObject, "ex", exObject);
				logger.warn(strStack);
			} finally {
				JSONUtils.put(callBackObject, "args", argsObject);
				rsWritable.setResult(callBackObject.toString());
				long cost = System.currentTimeMillis() - start;
				String msg = String.format("done task:%d,type:%s,status:%d,cost:%d", task.getId(), type,
						rsWritable.getStatus(), cost);
				logger.info(msg);
			}
			return rsWritable;
		}
	}

}
