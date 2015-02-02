package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.scope.ScriptableUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigJdBrandShop implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	public static final int SITE_ID = 1001;

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
		get.addHeader(
				"Cookie",
				"unionuuid=V2_ZAsQVBZeSxB3AEMEfklVV24CE1pHVRQRcQBEV3lNVFIIABNdR1dDFnELRVF6GllqZwISQkdTXBZwF0VUfAxJ; areaId=1; xtest=c.p.a69dc8ae32ca2fdcc3d60d333771c71f; mt_ext=%7b%22adu%22%3a%22fd0abf96426b0d8e2850ae645289b681%22%7d; ipLoc-djd=1-72-4137-0; ipLocation=%u5317%u4EAC; __jda=122270672.2082678400.1416134992.1419689279.1419699044.8; __jdb=122270672.4.2082678400|8.1419699044; __jdc=122270672; __jdv=122270672|direct|-|none|-; __jdu=2082678400");
		get.addHeader("Referer", url);
		System.err.println(url);
		String html = HttpClientUtils.getContent(client, get, "UTF-8");
		Document dom = Jsoup.parse(html, url);
		DataBean rsBean = new DataBean();
		Elements itemEls = dom.select("div.shop[shop-id]");
		if (!itemEls.isEmpty()) {
			String region = "";
			Set<String> brandSet = getBrandSet(dom, task);
			String mainBrand = getMainBrandName(brandSet);
			String synonym = toSynonym(brandSet);
			String brandCode = (String) task.get("brandCode");
			StringBuilder sb = new StringBuilder();
			Map<String, ShopVo> shopMap = new HashMap<String, ConfigJdBrandShop.ShopVo>();
			for (Element item : itemEls) {
				ShopVo shopVo = new ShopVo();
				shopVo.setShopCode(item.attr("shop-id"));
				shopVo.setBrandName(mainBrand);
				shopVo.setBrandUrl(url);
				shopVo.setBrandCode(brandCode == null ? getHashCode(mainBrand) : brandCode);
				shopVo.setSynonyms(synonym);
				shopVo.setRegion(region);
				rsBean.getDataList().add(shopVo);
				if (sb.length() > 0) {
					sb.append("%2C");
				}
				sb.append(shopVo.getShopCode());
				Elements countEls = item.select("div.related-products a:containsOwn(件)");
				if (!countEls.isEmpty()) {
					String sCount = countEls.first().ownText();
					shopVo.setSkuCount(Integer.valueOf(sCount.substring(0, sCount.length() - 1)));
				}
				shopMap.put(shopVo.getShopCode(), shopVo);
			}
			String sUrl = "http://search.jd.com/ShopName.php?ids=" + sb.toString();
			get = new HttpGet(sUrl);
			html = HttpClientUtils.getContent(client, get, "UTF-8");
			html = "var oShopArr =" + html;
			String source = html + "; var sArray = JSON.stringify(oShopArr);";
			Context cx = Context.enter();
			ScriptableObject scope = (ScriptableObject) cx.initStandardObjects((ScriptableObject) ScriptableUtils.getJSONScriptable());
			cx.evaluateString(scope, source, "cmd", 0, null);
			String sArray = Context.toString(ScriptableObject.getProperty(scope, "sArray"));
			Context.exit();
			JSONArray shopArray = new JSONArray(sArray);
			for (int i = 0; i < shopArray.length(); i++) {
				JSONObject shopObject = shopArray.getJSONObject(i);
				ShopVo shopVo = shopMap.get(JSONUtils.getString(shopObject, "id"));
				if (shopVo == null) {
					continue;
				}
				shopVo.setShopName(JSONUtils.getString(shopObject, "title"));
				shopVo.setShopUrl(JSONUtils.getString(shopObject, "url"));
				parserType(shopVo);
			}
		}
		addNextPages(dom, rsBean);
		return rsBean;
	}

	private String getUrl(TaskWritable task) throws UnsupportedEncodingException {
		Object urlObject = task.get("url");
		if (urlObject != null && urlObject.toString().indexOf("jd.com/pinpai/") > 0 && urlObject.toString().indexOf("vt=3#filter") > 0) {
			return urlObject.toString();
		}
		String brandCode = task.get("brandCode").toString();
		return "http://www.jd.com/pinpai/" + brandCode + ".html?enc=utf-8&vt=3#filter";
	}

	private void addNextPages(Document body, DataBean rsBean) {
		Elements oCurAs = body.select("#pagin-btm a.current[href]");
		if (oCurAs.isEmpty()) {
			return;
		}
		Integer iCurPage = Integer.valueOf(oCurAs.first().ownText());
		if (iCurPage != 1) {
			return;
		}
		Elements oNextAs = body.select("#pagin-btm a.next[href]:contains(下一页)");
		if (oNextAs.isEmpty()) {
			return;
		}
		String sNextUrl = oNextAs.first().absUrl("href");
		Pattern oReg = Pattern.compile("(page=)([0-9]+)(.*)");
		Matcher matcher = oReg.matcher(sNextUrl);
		if (!matcher.find()) {
			return;
		}
		Integer iPage = Integer.valueOf(matcher.group(2));
		if (iPage != 2) {
			return;
		}

		Elements oPageAs = body.select("#pagin-btm span.page-skip em:containsOwn(页)");
		if (oPageAs.isEmpty()) {
			return;
		}
		String sTxt = oPageAs.first().ownText();
		oReg = Pattern.compile("(共)([0-9]+)(页)");
		matcher = oReg.matcher(sTxt);
		if (!matcher.find()) {
			return;
		}
		rsBean.getNextList().add(sNextUrl);
		System.out.println("sNextUrl:" + sNextUrl);
		Integer iTotal = Integer.valueOf(matcher.group(2));
		for (int jj = 3; jj <= iTotal; jj++) {
			String sCurUrl = sNextUrl.replace("page=2", "page=" + jj);
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
