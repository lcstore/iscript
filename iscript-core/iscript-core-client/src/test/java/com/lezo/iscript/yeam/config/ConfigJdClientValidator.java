package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigJdClientValidator implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigJdClientValidator.class);
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject gObject = new JSONObject();
		JSONObject itemObject = getDataObject(task, gObject);
		System.out.println(itemObject);
		JSONUtils.put(gObject, "rs", itemObject.toString());
		doCollect(gObject, task);
		return "";
	}

	private void doCollect(JSONObject gObject, TaskWritable task) {
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);

		JSONUtils.put(gObject, "args", argsObject);

		System.err.println("gObject:" + gObject);
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}

	private JSONObject getDataObject(TaskWritable task, JSONObject gObject) throws Exception {
		String url = (String) task.get("url");
		int max = 5;
		int index = 0;
		ClientValidate cValidate = new ClientValidate();
		while (true) {
			if (url.indexOf("?ld_ld=") > 0) {
				url = url.substring(0, url.indexOf("?ld_ld=")) + "?ld_ld=" + Math.random();
			} else {
				url = url + "?ld_ld=" + Math.random();
			}
			HttpGet get = new HttpGet(url);
			System.out.println("url:" + url);
			get.addHeader("refer", url);
			get.getParams().setParameter(AllClientPNames.HANDLE_REDIRECTS, false);
			HttpResponse resp = client.execute(get);
			Header[] headers = resp.getHeaders("Location");
			if (headers != null && headers.length > 0) {
				task.getArgs().remove("retry");
				throw new HttpStatusException("get abort url:" + headers[0], resp.getStatusLine().getStatusCode(), url);
			}
			String html = EntityUtils.toString(resp.getEntity(), "UTF-8");
			if (html.indexOf("Hello World") > 0) {
				cValidate.setStatus(1);
				JSONObject rsObject = new JSONObject();
				JSONUtils.put(rsObject, "retry", index);
				JSONUtils.put(rsObject, "html", html);
				cValidate.setReason(rsObject.toString());
				break;
			}
			if (++index == max) {
				cValidate.setStatus(0);
				JSONObject rsObject = new JSONObject();
				JSONUtils.put(rsObject, "retry", index);
				JSONUtils.put(rsObject, "html", html);
				cValidate.setReason(rsObject.toString());
				break;
			}
			TimeUnit.MILLISECONDS.sleep(1000);
		}
		ResultBean rsBean = new ResultBean();
		rsBean.getDataList().add(cValidate);
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	private final class ClientValidate {
		private int status = 0;
		private String reason;

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}
	}

	private final class ResultBean {
		private List<Object> dataList = new ArrayList<Object>();
		private List<Object> nextList = new ArrayList<Object>();

		public List<Object> getDataList() {
			return dataList;
		}
	}
}
