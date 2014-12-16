package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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

public class ConfigTmallBrandShop implements ConfigParser {
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
		Elements itemEls = dom.select("div.brandShop ul.brandShop-slide-list a[href][target]");
		if (!itemEls.isEmpty()) {
			BrandShop tBean = new BrandShop();
			rsBean.getDataList().add(tBean);
			List<ShopVo> shopList = new ArrayList<ConfigTmallBrandShop.ShopVo>();
			tBean.setShopList(shopList);
			for (Element item : itemEls) {
				ShopVo shopVo = new ShopVo();
				shopVo.setShopUrl(item.attr("href"));
				shopVo.setShopName(item.text().trim());
				parserType(shopVo);
				shopList.add(shopVo);
			}
			addBrands(tBean, dom, task);
			addRegion(tBean, dom);
		}
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	private void addRegion(BrandShop tBean, Document dom) {
		Elements destEls = dom.select("div.brandWiki ul.brandWiki-con li:contains(发源地) em");
		if (!destEls.isEmpty()) {
			tBean.setRegion(destEls.first().ownText().trim());
		}
	}

	private void addBrands(BrandShop tBean, Document dom, TaskWritable task) {
		Elements destEls = dom.select("div.brandWiki ul.brandWiki-con li:contains(品牌名) em");
		String sText = destEls.first().ownText();
		// Bejirog/北极绒
		Set<String> brandSet = new HashSet<String>();
		String[] sArr = sText.split("/");
		for (String sUnit : sArr) {
			brandSet.add(sUnit.toLowerCase());
		}
		String brandName = (String) task.get("brandName");
		if (StringUtils.isNotEmpty(brandName)) {
			// 夏新(Amoi)
			int fromIndex = brandName.indexOf("(");
			if (fromIndex < 0) {
				brandSet.add(brandName.toLowerCase());
			} else {
				int toIndex = brandName.indexOf(")");
				String leftString = brandName.substring(0, fromIndex);
				brandSet.add(leftString.toLowerCase());
				String rightString = brandName.substring(fromIndex + 1, toIndex);
				brandSet.add(rightString.toLowerCase());
			}
		}
		tBean.setBrandSet(brandSet);
	}

	private void parserType(ShopVo shopVo) {
		// 0-旗舰店，1-专卖店，2-专营店，3-其他
		if (StringUtils.isEmpty(shopVo.getShopName())) {
			shopVo.setShopType(3);
		} else if (shopVo.getShopName().indexOf("旗舰") > 0) {
			shopVo.setShopType(0);
		} else if (shopVo.getShopName().indexOf("专卖") > 0) {
			shopVo.setShopType(1);
		} else if (shopVo.getShopName().indexOf("专营") > 0) {
			shopVo.setShopType(2);
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

	private static class ShopVo {
		private String shopName;
		private String shopUrl;
		private Integer shopType = 3;

		public String getShopName() {
			return shopName;
		}

		public void setShopName(String shopName) {
			this.shopName = shopName;
		}

		public String getShopUrl() {
			return shopUrl;
		}

		public void setShopUrl(String shopUrl) {
			this.shopUrl = shopUrl;
		}

		public Integer getShopType() {
			return shopType;
		}

		public void setShopType(Integer shopType) {
			this.shopType = shopType;
		}
	}

	private static class BrandShop {
		private String region;
		private Set<String> brandSet;
		private List<ShopVo> shopList;

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public Set<String> getBrandSet() {
			return brandSet;
		}

		public void setBrandSet(Set<String> brandSet) {
			this.brandSet = brandSet;
		}

		public List<ShopVo> getShopList() {
			return shopList;
		}

		public void setShopList(List<ShopVo> shopList) {
			this.shopList = shopList;
		}

	}

	private final static class ResultBean {
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
