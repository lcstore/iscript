package com.lezo.iscript.yeam.config;

import java.io.StringWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigGoogleChecker implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigGoogleChecker.class);
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

	public ConfigGoogleChecker() {
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		DataBean dataBean = getDataObject(task);
		return convert2TaskCallBack(dataBean, task);
	}

	private String convert2TaskCallBack(DataBean dataBean, TaskWritable task) throws Exception {
		if (dataBean == null) {
			return JSONUtils.EMPTY_JSONOBJECT;
		}
		dataBean.getTargetList().add("UrlCheckDto");

		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, dataBean);
		String dataString = writer.toString();

		JSONObject returnObject = new JSONObject();
		JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, JSONUtils.EMPTY_JSONOBJECT);
		JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
		return returnObject.toString();
	}

	private DataBean getDataObject(TaskWritable task) throws Exception {
		String url = (String) task.get("url");
		UrlCheckDto checkDto = new UrlCheckDto();
		checkDto.setUrl(url);
		DataBean rsBean = new DataBean();
		rsBean.getDataList().add(checkDto);
		try {
			long startMills = System.currentTimeMillis();
			HttpGet get = new HttpGet(url);
			HttpResponse respone = client.execute(get);
			checkDto.setStatusCode(respone.getStatusLine().getStatusCode());
			String html = EntityUtils.toString(respone.getEntity(), "UTF-8");
			checkDto.setLength(html == null ? -1 : html.length());
			if (html.indexOf("searchform") > 0) {
				checkDto.setValidate(true);
			}
			checkDto.setCostMills(System.currentTimeMillis() - startMills);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (checkDto.isValidate()) {
			return rsBean;
		}
		return null;
	}

	private static class UrlCheckDto {
		private String url;
		private boolean isValidate;
		private int statusCode;
		private long length;
		private long costMills;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public long getLength() {
			return length;
		}

		public void setLength(long length) {
			this.length = length;
		}

		public boolean isValidate() {
			return isValidate;
		}

		public void setValidate(boolean isValidate) {
			this.isValidate = isValidate;
		}

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public long getCostMills() {
			return costMills;
		}

		public void setCostMills(long costMills) {
			this.costMills = costMills;
		}
	}
}
