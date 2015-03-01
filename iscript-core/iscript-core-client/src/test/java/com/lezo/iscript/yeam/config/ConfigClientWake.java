package com.lezo.iscript.yeam.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.storage.ResultFutureStorager;
import com.lezo.iscript.yeam.task.TaskWorker;
import com.lezo.iscript.yeam.task.TasksCaller;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigClientWake implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigClientWake.class);
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private List<String> clientList;
	private Timer timer;

	public ConfigClientWake() {
		clientList = new ArrayList<String>();
//		clientList.add("http://client1001.sturgeon.mopaas.com/");
//		clientList.add("http://resulter.sturgeon.mopaas.com/");
//		clientList.add("http://e2002.sturgeon.mopaas.com/");
//		clientList.add("http://e2001.sturgeon.mopaas.com/");
//		clientList.add("http://e1001.sturgeon.mopaas.com/");
//		clientList.add("http://e1002.sturgeon.mopaas.com/");
//		clientList.add("http://p1001.sturgeon.mopaas.com/");
//		clientList.add("http://p1002.sturgeon.mopaas.com/");

		
		clientList.add("http://dl1001.sturgeon.mopaas.com/");
		clientList.add("http://dl1002.sturgeon.mopaas.com/");
		clientList.add("http://e1001.sturgeon.mopaas.com/");
		clientList.add("http://e1002.sturgeon.mopaas.com/");
		clientList.add("http://p1001.sturgeon.mopaas.com/");
		clientList.add("http://p1002.sturgeon.mopaas.com/");
		clientList.add("http://e2001.sturgeon.mopaas.com/");
		clientList.add("http://e2002.sturgeon.mopaas.com/");
		clientList.add("http://dlinked.sturgeon.mopaas.com/");
		clientList.add("http://lcstore.sturgeon.mopaas.com/");
		clientList.add("http://idober.jd-app.com/");
		clientList.add("http://iclient.jd-app.com/");
		clientList.add("http://momo.jd-app.com/");
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					TaskWritable taskWritable = new TaskWritable();
					taskWritable.put("type", getName());
					ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
					ResultFutureStorager storager = ResultFutureStorager.getInstance();
					Future<ResultWritable> future = caller.submit(new TaskWorker(taskWritable));
					storager.getStorageBuffer().add(future);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 30 * 60 * 1000);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONArray listArray = new JSONArray();
		int index = 0;
		int total = clientList.size();
		for (String url : clientList) {
			logger.info(String.format("wake url:%s,index:%d/%d", url, ++index, total));
			wakeClient(listArray, url);
		}
		String result = listArray.toString();
		logger.info("wake.rs:" + result);
		return result;
	}

	private void wakeClient(JSONArray listArray, String url) {
		JSONObject itemObject = new JSONObject();
		HttpGet get = new HttpGet(url);
		long start = System.currentTimeMillis();
		int status = 0;
		try {
			HttpContext context = new BasicHttpContext();
			HttpResponse res = client.execute(get, context);
			int statusCode = res.getStatusLine().getStatusCode();
			String html = EntityUtils.toString(res.getEntity());
			status = getStatus(statusCode, html);
		} catch (Exception e) {
			status = 0;
			String msg = ExceptionUtils.getStackTrace(e);
			JSONUtils.put(itemObject, "ex", msg);
			logger.warn(String.format("client url:%s,cause:%s", url, msg));
		} finally {
			if (get != null && !get.isAborted()) {
				get.abort();
			}
		}
		long cost = System.currentTimeMillis() - start;
		JSONUtils.put(itemObject, "url", url);
		JSONUtils.put(itemObject, "cost", cost);
		JSONUtils.put(itemObject, "status", status);
		JSONUtils.put(itemObject, "detector", HeaderUtils.CLIENT_NAME);
		listArray.put(itemObject);
	}

	private int getStatus(int statusCode, String html) {
		if (statusCode < 200 || statusCode >= 300) {
			return 0;
		}
		return 1;
	}

}
