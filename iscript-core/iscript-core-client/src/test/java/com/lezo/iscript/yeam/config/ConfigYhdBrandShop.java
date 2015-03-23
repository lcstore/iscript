package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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

public class ConfigYhdBrandShop implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	public static final int SITE_ID = 1002;

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		DataBean itemObject = getDataObject(task);
		return convert2TaskCallBack(itemObject, task);
	}

	private String convert2TaskCallBack(DataBean dataBean, TaskWritable task) throws Exception {
		JSONObject returnObject = new JSONObject();
		JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, JSONUtils.EMPTY_JSONOBJECT);
		if (dataBean != null) {
			dataBean.getTargetList().add("BrandConfigVo");
			dataBean.getTargetList().add("BrandShopDto");

			ObjectMapper mapper = new ObjectMapper();
			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, dataBean);
			String dataString = writer.toString();

			JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
		}
		return returnObject.toString();
	}

	private DataBean getDataObject(TaskWritable task) throws Exception {
		String url = getUrl(task);
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get, "UTF-8");
		Document dom = Jsoup.parse(html, url);
		DataBean rsBean = new DataBean();
		Elements itemEls = dom.select("div.search_item_box a[id^=merchant_][href][target]");
		if (!itemEls.isEmpty()) {
			String region = "";
			Set<String> brandSet = getBrandSet(dom, task);
			String mainBrand = getMainBrandName(brandSet);
			String synonym = toSynonym(brandSet);
			String brandCode = (String) task.get("brandCode");
			Pattern oReg = Pattern.compile(".*?m-([0-9]+)\\.html");
			Elements countEls = dom.select("small.result_count:contains(条)");
			Integer iCount=null;
			if (!countEls.isEmpty()) {
				String sCount = countEls.first().ownText();
				sCount = sCount.replace("共", "");
				sCount = sCount.replace("条", "");
				iCount = (Integer.valueOf(sCount));
			}
			Set<String> shopSet = new HashSet<String>();
			addShops(rsBean,oReg,iCount,dom,shopSet);
			if (itemEls.size() >= 29 && url.indexOf("isGetMoreProducts") < 0) {
				String moreUrl = url + "?callback=jsonp" + System.currentTimeMillis()
						+ "&isGetMoreProducts=1&moreProductsDefaultTemplate=0&isLargeImg=0";
				get = new HttpGet(moreUrl);
				get.addHeader("Referer", url);
				html = HttpClientUtils.getContent(client, get);
                Document moreDom = Jsoup.parse(html);
                addShops(rsBean,oReg,iCount,moreDom,shopSet);
			}
			for(Object data:rsBean.getDataList()){
				ShopVo shopVo = (ShopVo) data;
				shopVo.setBrandName(mainBrand);
				shopVo.setBrandUrl(url);
				shopVo.setBrandCode(brandCode == null ? getHashCode(mainBrand) : brandCode);
				shopVo.setSynonyms(synonym);
				shopVo.setRegion(region);
				shopVo.setSkuCount(iCount);
			}

		}
		addNextPages(dom, rsBean);
		return rsBean;
	}

	private void addShops(DataBean rsBean, Pattern oReg, Integer iCount, Document dom, Set<String> shopSet) {
		Elements itemEls = dom.select("div.search_item_box a[id^=merchant_][href][target]");
		for (Element item : itemEls) {
			if (shopSet.contains(item.ownText())) {
				continue;
			}
			shopSet.add(item.ownText());
			ShopVo shopVo = new ShopVo();
			rsBean.getDataList().add(shopVo);
			shopVo.setShopName(item.ownText());
			shopVo.setShopUrl(item.absUrl("href"));
			parserType(shopVo);
			Matcher matcher = oReg.matcher(shopVo.getShopUrl());
			if (matcher.find()) {
				shopVo.setShopCode(matcher.group(1));
			}
		}
	}

	private String getUrl(TaskWritable task) throws UnsupportedEncodingException {
		Object urlObject = task.get("url");
		return urlObject.toString();
	}

	private void addNextPages(Document body, DataBean rsBean) {
		Elements pageCountEls = body.select("#pageCountPage");
		if(pageCountEls.isEmpty()){
		   return; 	
		}
		Elements oNextAs = body.select("a.page_next[href]:contains(下一页)");
		if (oNextAs.isEmpty()) {
			return;
		}
		String sNextUrl = oNextAs.first().absUrl("href");
		rsBean.getNextList().add(sNextUrl);
		System.out.println("sNextUrl:" + sNextUrl);
		Integer iTotal = Integer.valueOf(pageCountEls.first().val());
		for (int jj = 3; jj <= iTotal; jj++) {
			String sCurUrl = sNextUrl.replace("-p2-", "-p"+jj+"-");
			rsBean.getNextList().add(sCurUrl);
			System.out.println("sNextUrl:" + sCurUrl);
		}
	}

	private String toSynonym(Set<String> brandSet) {
		StringBuilder sb = new StringBuilder();
		for (String brand : brandSet) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(brand);
		}
		return sb.toString();
	}

	private String getHashCode(String mainBrand) {
		String hCode = "" + mainBrand.hashCode();
		return hCode.replace("-", "H");
	}

	private String getMainBrandName(Set<String> brandSet) {
		Pattern oReg = Pattern.compile(".*?[\u4e00-\u9fa5]+.*");
		String main = null;
		for (String brand : brandSet) {
			Matcher matcher = oReg.matcher(brand);
			if (matcher.find()) {
				if (main == null) {
					main = brand;
				} else if (main.length() < brand.length()) {
					main = brand;
				}
			}
		}
		if (main == null) {
			for (String brand : brandSet) {
				if (main == null) {
					main = brand;
				} else if (main.length() < brand.length()) {
					main = brand;
				}
			}
		}
		return main;
	}

	private String getRegion(Document dom) {
		Elements destEls = dom.select("div.brandWiki ul.brandWiki-con li:contains(发源地) em");
		if (!destEls.isEmpty()) {
			return destEls.first().ownText().trim();
		}
		return null;
	}

	private Set<String> getBrandSet(Document dom, TaskWritable task) {
		// Bejirog/北极绒
		Set<String> brandSet = new HashSet<String>();
		String brandName = (String) task.get("brandName");
		brandName = brandName.replace("（", "(");
		brandName = brandName.replace("）", ")");
		if (StringUtils.isNotEmpty(brandName)) {
			// 夏新(Amoi)
			int fromIndex = brandName.indexOf("(");
			if (fromIndex < 0) {
				String[] sArr = brandName.split("/");
				for (String sUnit : sArr) {
					brandSet.add(sUnit.toLowerCase());
				}
			} else {
				int toIndex = brandName.indexOf(")");
				String leftString = brandName.substring(0, fromIndex);
				brandSet.add(leftString.toLowerCase());
				String rightString = brandName.substring(fromIndex + 1, toIndex);
				brandSet.add(rightString.toLowerCase());
			}
		}
		return brandSet;
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

	private static class ShopVo {
		private Integer siteId = SITE_ID;
		private String brandCode;
		private String brandName;
		private String brandUrl;
		private String synonyms;
		private Integer skuCount;
		private String region;
		// private Date createTime;
		// private Date updateTime;
		private String shopName;
		private String shopCode;
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

		public Integer getSiteId() {
			return siteId;
		}

		public void setSiteId(Integer siteId) {
			this.siteId = siteId;
		}

		public String getBrandCode() {
			return brandCode;
		}

		public void setBrandCode(String brandCode) {
			this.brandCode = brandCode;
		}

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

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getSynonyms() {
			return synonyms;
		}

		public void setSynonyms(String synonyms) {
			this.synonyms = synonyms;
		}

		public Integer getSkuCount() {
			return skuCount;
		}

		public void setSkuCount(Integer skuCount) {
			this.skuCount = skuCount;
		}

		public String getShopCode() {
			return shopCode;
		}

		public void setShopCode(String shopCode) {
			this.shopCode = shopCode;
		}
	}
}
