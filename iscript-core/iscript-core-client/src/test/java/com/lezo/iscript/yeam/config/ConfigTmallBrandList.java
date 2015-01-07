package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
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
		Document dom = Jsoup.parse(html, url);
		ResultBean rsBean = new ResultBean();
		Elements itemEls = dom.select("#J_SearchResult li a[href][target]");
		if (itemEls.isEmpty()) {
			itemEls = dom.select("div.brandItem div.brandItem-info a[href*=brand.tmall.com/brandInfo.htm][target]");
			Pattern oReg = Pattern.compile("(brandId=)([0-9]+)");
			for (Element item : itemEls) {
				BrandList tBean = new BrandList();
				tBean.setBrandUrl(item.absUrl("href"));
				Element sibEle = item.nextElementSibling();
				if (sibEle != null && sibEle.hasText()) {
					tBean.setBrandName(sibEle.ownText().trim());
				}
				Matcher matcher = oReg.matcher(tBean.getBrandUrl());
				if (matcher.find()) {
					tBean.setBrandCode(matcher.group(2));
				}
				rsBean.getDataList().add(tBean);
			}
		} else {
			for (Element item : itemEls) {
				BrandList tBean = new BrandList();
				tBean.setBrandName(item.ownText().trim());
				tBean.setBrandUrl(item.absUrl("href"));
				Element sibEle = item.nextElementSibling();
				if (sibEle != null && sibEle.hasAttr("data-brandid")) {
					tBean.setBrandCode(sibEle.attr("data-brandid"));
				}
				unifyName(tBean);
				rsBean.getDataList().add(tBean);
			}
		}
		addNexts(rsBean, dom);
		addHomeBrand(rsBean, dom);
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	private void addHomeBrand(ResultBean rsBean, Document dom) {
		addBrands(rsBean, dom);
		Elements areaEls = dom.select("div.brandFp div.brand-right div.j_BrandFloor textarea");
		if (!areaEls.isEmpty()) {
			for (Element ele : areaEls) {
				Document subDom = Jsoup.parse(ele.text());
				addBrands(rsBean, subDom);
			}
		}
	}

	private void addBrands(ResultBean rsBean, Document dom) {
		Elements brandList = dom.select("li.bFlis-con-list a.bFlis-con-mask");
		for (Element bEle : brandList) {
			BrandList tBean = new BrandList();
			tBean.setBrandName(bEle.select(".bFlisc-mask-shop").first().text());
			tBean.setBrandCode(bEle.select(".bFlisc-mask-add[data-brandid]").first().attr("data-brandid"));
			unifyName(tBean);
			tBean.setBrandUrl(String.format("http://brand.tmall.com/brandInfo.htm?brandId=%s&type=0", tBean.getBrandCode()));
			rsBean.getDataList().add(tBean);
		}
		brandList = dom.select("a.bFlil-link[title][href]");
		for (Element bEle : brandList) {
			BrandList tBean = new BrandList();
			tBean.setBrandName(bEle.attr("title"));
			Elements idEls = bEle.select("b[data-brandid]");
			tBean.setBrandCode(idEls.first().attr("data-brandid"));
			unifyName(tBean);
			tBean.setBrandUrl(String.format("http://brand.tmall.com/brandInfo.htm?brandId=%s&type=0", tBean.getBrandCode()));
			rsBean.getDataList().add(tBean);
		}
	}

	private void unifyName(BrandList tBean) {
		String brandName = tBean.getBrandName();
		brandName = brandName.replace("(", "/");
		brandName = brandName.replace(")", "");
		tBean.setBrandName(brandName);
	}

	private void addNexts(ResultBean rsBean, Document dom) {
		Elements pageEls = dom.select("div#content b.ui-page-s-len:contains(页)");
		if (pageEls.isEmpty()) {
			return;
		}
		Pattern oReg = Pattern.compile("([0-9]+)/([0-9]+)");
		Matcher matcher = oReg.matcher(pageEls.first().text());
		if (!matcher.find()) {
			return;
		}
		Integer curPage = Integer.valueOf(matcher.group(1));
		if (curPage != 1) {
			return;
		}
		Integer totalPage = Integer.valueOf(matcher.group(2));
		Elements nextEls = dom.select("#content a.mui-page-next[href*=page=2]");
		if (nextEls.isEmpty()) {
			return;
		}
		String nextUrl = nextEls.first().absUrl("href");
		List<Object> nextList = rsBean.getNextList();
		nextList.add(nextUrl);
		for (int i = 3; i <= totalPage; i++) {
			String sNextString = nextUrl.replace("page=2", "page=" + i);
			nextList.add(sNextString);
		}
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
