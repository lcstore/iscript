package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigTmallBrandList implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static final String EMTPY_RESULT = new JSONObject().toString();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject itemObject = getDataObject(task);
		doCollect(itemObject, task);
		return itemObject.toString();
	}

	private JSONObject getDataObject(TaskWritable task) throws Exception {
		String url = task.get("url").toString();
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", url);
		String html = HttpClientUtils.getContent(client, get, "gbk");
		Document dom = Jsoup.parse(html);
		ResultBean rsBean = new ResultBean();
		Elements itemEls = dom.select("#J_SearchResult li a[href][target]");
		for (Element item : itemEls) {
			BrandList tBean = new BrandList();
			tBean.setBrandName(item.ownText().trim());
			tBean.setBrandUrl(item.absUrl("href"));
			Element sibEle = item.nextElementSibling();
			if (sibEle != null && sibEle.hasAttr("data-brandid")) {
				tBean.setBrandCode(sibEle.attr("data-brandid"));
			}
			rsBean.getDataList().add(tBean);
		}
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	private void doCollect(JSONObject dataObject, TaskWritable task) {
		JSONObject gObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);

		JSONArray tArray = new JSONArray();
		tArray.put("ProductDto");
		tArray.put("ProductStatDto");
		JSONUtils.put(argsObject, "target", tArray);
		JSONUtils.put(gObject, "args", argsObject);

		JSONUtils.put(gObject, "rs", dataObject.toString());
		System.err.println("data:" + dataObject);
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}

	private class BrandList {
		private String brandName;
		private String brandUrl;
		private String brandCode;

		public String getBrandName() {
			return brandName;
		}

		public void setBrandName(String brandName) {
			this.brandName = brandName;
		}

		public String getBrandUrl() {
			return brandUrl;
		}

		public void setBrandUrl(String brandUrl) {
			this.brandUrl = brandUrl;
		}

		public String getBrandCode() {
			return brandCode;
		}

		public void setBrandCode(String brandCode) {
			this.brandCode = brandCode;
		}

	}

	private final class ResultBean {
		private List<Object> dataList = new ArrayList<Object>();
		private List<Object> nextList = new ArrayList<Object>();

		public List<Object> getDataList() {
			return dataList;
		}

		public void setDataList(List<Object> dataList) {
			this.dataList = dataList;
		}

		public List<Object> getNextList() {
			return nextList;
		}

		public void setNextList(List<Object> nextList) {
			this.nextList = nextList;
		}

	}
}
