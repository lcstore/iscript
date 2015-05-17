package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigJdBrandList implements ConfigParser {
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
		String html = HttpClientUtils.getContent(client, get, "UTF-8");
		Document dom = Jsoup.parse(html, url);
		ResultBean rsBean = new ResultBean();
		Elements itemEls = dom.select("div[id^=brand_id_] a[href][title]");
		if (!itemEls.isEmpty()) {
			for (Element item : itemEls) {
				BrandList tBean = new BrandList();
				tBean.setBrandUrl(item.absUrl("href"));
				tBean.setBrandName(item.attr("title"));
				String idString = item.parent().id();
				tBean.setBrandCode(idString.replace("brand_id_", ""));
				if (tBean.getBrandName().equals("其它")) {
					continue;
				}
//				unifyName(tBean);
				rsBean.getDataList().add(tBean);
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	private void unifyName(BrandList tBean) {
		String brandName = tBean.getBrandName();
		// brandName = brandName.replace("(", "/");
		// brandName = brandName.replace(")", "");
		brandName = brandName.replace("（", "/");
		brandName = brandName.replace("）", "");
		tBean.setBrandName(brandName);
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

		public void setBrandUrl(String brandUrl) {
			this.brandUrl = brandUrl;
		}

		public void setBrandCode(String brandCode) {
			this.brandCode = brandCode;
		}

		public String getBrandUrl() {
			return brandUrl;
		}

		public String getBrandCode() {
			return brandCode;
		}

	}

	private final class ResultBean {
		private List<Object> dataList = new ArrayList<Object>();
		private List<Object> nextList = new ArrayList<Object>();

		public List<Object> getDataList() {
			return dataList;
		}

		public List<Object> getNextList() {
			return nextList;
		}

	}
}
