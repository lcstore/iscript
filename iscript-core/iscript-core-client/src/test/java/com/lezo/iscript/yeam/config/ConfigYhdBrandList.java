package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigYhdBrandList implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

	// http://www.yhd.com/brand/c8644.html
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
//		dataBean.getTargetList().add("ProxyAddrDto");

		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, dataBean);
		String dataString = writer.toString();

		JSONObject returnObject = new JSONObject();
		JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, dataString);
//		JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
		return returnObject.toString();
	}

	private DataBean getDataObject(TaskWritable task) throws Exception {
		String url = task.get("url").toString();
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", url);
		String html = HttpClientUtils.getContent(client, get, "UTF-8");
		Document dom = Jsoup.parse(html, url);
		DataBean rsBean = new DataBean();
		Elements itemEls = dom.select("div.brand_list li a.item[href],div.other_brand li a.item[href]");
		if (!itemEls.isEmpty()) {
			for (Element item : itemEls) {
				BrandList tBean = new BrandList();
				tBean.setBrandName(item.text().trim());
				Pattern oReg = Pattern.compile("www.yhd.com/brand/([0-9]+)");
				String sUrl = item.absUrl("href");
				Matcher matcher = oReg.matcher(sUrl);
				if (!matcher.find()) {
					System.err.println("unknown:" + sUrl);
					continue;
				}
				tBean.setBrandCode(matcher.group(1));
				tBean.setBrandUrl("http://list.yhd.com/b" + tBean.getBrandCode());
				// unifyName(tBean);
				rsBean.getDataList().add(tBean);
			}
		}
		return rsBean;
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

		public String getBrandCode() {
			return brandCode;
		}

		public String getBrandUrl() {
			return brandUrl;
		}

	}
}
