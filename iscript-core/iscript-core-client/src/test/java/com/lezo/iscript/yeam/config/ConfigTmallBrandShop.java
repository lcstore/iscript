package com.lezo.iscript.yeam.config;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
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

import com.lezo.iscript.proxy.ProxyClientUtils;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigTmallBrandShop implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	public static final int SITE_ID = 1013;

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		// brand shop list.
		// http://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.7.QRTGLw&brand=107380&sort=s&style=w#J_Filter
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
			if (CollectionUtils.isNotEmpty(dataBean.getNextList())) {
				JSONObject rsObject = JSONUtils.getJSONObject(dataString);
				rsObject.remove("targetList");
				rsObject.remove("dataList");
				JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, rsObject.toString());
			}
		}
		return returnObject.toString();
	}

	private DataBean getDataObject(TaskWritable task) throws Exception {
		String url = task.get("url").toString();
		File file = new File("src/test/resources/data/proxy.txt");
		if (file.exists()) {
			List<String> proxyList = FileUtils.readLines(file);
			proxyList.add("");
			int index = new Random().nextInt(proxyList.size());
			String proxy = proxyList.get(index);
			if (!StringUtils.isBlank(proxy)) {
				String[] proxyArr = proxy.trim().split(":");
				task.put("proxyHost", proxyArr[0]);
				task.put("proxyPort", Integer.valueOf(proxyArr[1]));
				task.put("proxyType", 1);
			}

		}
		TimeUnit.SECONDS.sleep(new Random().nextInt(60));
		// HttpGet get = new HttpGet(url);
		HttpGet get = ProxyClientUtils.createHttpGet(url, task);
		get.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		get.addHeader("Accept-Encoding", "gzip, deflate");
		get.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0");
		get.addHeader(
				"Cookie",
				"cna=IhdRDRpyYTYCAdOh+Rh5Glt6; otherx=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0; x=__ll%3D-1%26_ato%3D0; CNZZDATA1000279581=1211739643-1422628013-http%253A%252F%252Fdetail.tmall.com%252F%7C1428209984; cq=ccp%3D0; isg=C6DC626A355F35EC98CE561B1B8A0BC2; pnm_cku822=204UW5TcyMNYQwiAiwZTXFIdUh1SHJOe0BuOG4%3D%7CUm5Ockt0QX5Cf0pxSXFFcCY%3D%7CU2xMHDJ%2BH2QJZwBxX39Rb1R6WnQoSS9DJFogDlgO%7CVGhXd1llXGNWaVVoXWZeZlJnUG1PdEF5QX9Lckx5RX1FekF%2BQW85%7CVWldfS0RMQ47Dy8RMR9lFkZoPmg%3D%7CVmhIGCQYLQ0wECwYJRs7BzoDOwYmGi4RLAwwDTgFJRktEi8PMw43ClwK%7CV25Tbk5zU2xMcEl1VWtTaUlwJg%3D%3D; l=AXtJxEB4-2U7JS7FfxHqZjslW-67pvtl; t=38f8eaef73d3f36a372d6a1da704d12a; res=scroll%3A1903*10450-client%3A1903*586-offset%3A1903*10450-screen%3A1920*1080; swfstore=284800; whl=-1%260%260%260; tt=sec.taobao.com; _tb_token_=4Uxl5G4oeqxO; ck1=; uc1=lltime=1428201238&cookie14=UoW1HJ2O0%2FKldw%3D%3D&existShop=false&cookie16=U%2BGCWk%2F74Mx5tgzv3dWpnhjPaQ%3D%3D&cookie21=WqG3DMC9Fb5mPLIRJbWy&tag=0&cookie15=URm48syIIVrSKA%3D%3D&pas=0; uc3=nk2=E63VQkPQIzE%3D&id2=UonaUgm3h6aFZQ%3D%3D&vt3=F8dAT%2BauXHAfsTiN66E%3D&lg2=VT5L2FSpMGV7TQ%3D%3D; lgc=pis_1001; tracknick=pis_1001; cookie2=b0412ba4531056024cd4f3a5d9ddde0c; cookie1=UNiEt3GrKrqAFyfwCdFMB1SvDybA8ecJHzHb5vnHRoo%3D; unb=1817804237; _nk_=pis_1001; _l_g_=Ug%3D%3D; cookie17=UonaUgm3h6aFZQ%3D%3D; login=true");
		String html = HttpClientUtils.getContent(client, get, "gbk");
		int index = html.indexOf("search_shopitem");
		index = index < 0 ? html.indexOf("productShop-name") : index;
		if (index < 0) {
			FileUtils.writeStringToFile(new File("src/test/resources/tmall.txt"), JSONUtils.getJSONObject(task.getArgs()) + "\n" + html);
			throw new RuntimeException("can not found search_shopitem.args:" + JSONUtils.getJSONObject(task.getArgs()));
		}
		Document dom = Jsoup.parse(html, url);

		DataBean rsBean = new DataBean();
		Elements itemEls = dom.select("div.brandShop ul.brandShop-slide-list a[href][target],div.shopHeader-info a.sHi-title[href],a.productShop-name[href]");
		if (!itemEls.isEmpty()) {
			Pattern oPattern = Pattern.compile("brand=([0-9]+)");
			Matcher matcher = oPattern.matcher(url);
			String region = getRegion(dom);
			Set<String> brandSet = getBrandSet(dom, task);
			String mainBrand = getMainBrandName(brandSet);
			String synonym = brandSet.toString();
			String brandCode = matcher.find() ? matcher.group(1) : (String) task.get("brandCode");
			for (Element item : itemEls) {
				ShopVo shopVo = new ShopVo();
				shopVo.setShopUrl(item.absUrl("href"));
				if (item.hasClass("sHi-title") || item.hasClass("productShop-name")) {
					shopVo.setShopName(item.ownText().trim());
				} else {
					shopVo.setShopName(item.select("h3").first().text().trim());
				}
				parserType(shopVo);
				shopVo.setBrandName(mainBrand);
				shopVo.setBrandUrl(url);
				shopVo.setBrandCode(brandCode == null ? getHashCode(mainBrand) : brandCode);
				shopVo.setSynonyms(synonym);
				shopVo.setRegion(region);
				rsBean.getDataList().add(shopVo);
			}
			addNexts(rsBean, dom);
		}
		return rsBean;
	}

	private void addNexts(DataBean rsBean, Document dom) {
		String sUrl = dom.baseUri();
		if (dom.baseUri().indexOf("&s=") > 0) {
			return;
		}
		Elements totalEls = dom.select("input[name=totalPage]");
		if (totalEls.isEmpty()) {
			return;
		}
		int totalPage = Integer.valueOf(totalEls.first().val());
		if (totalPage <= 1) {
			return;
		}
		for (int i = 2; i <= totalPage; i++) {
			rsBean.getNextList().add(sUrl.replace("&sort=s", "&s=" + (i - 1) * 20 + "&sort=s"));
		}

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
		Elements destEls = dom.select("div.brandWiki ul.brandWiki-con li:contains(品牌名) em");
		destEls = destEls.isEmpty() ? dom.select("#J_CrumbSlideCon .crumbAttr[data-tag=brand][title]") : destEls;
		// Bejirog/北极绒
		Set<String> brandSet = new HashSet<String>();
		if (!destEls.isEmpty()) {
			Element brandElement = destEls.first();
			String sText = brandElement.hasClass("crumbAttr") ? brandElement.attr("title") : brandElement.ownText();
			sText = sText.replace("品牌:", "");
			String[] sArr = sText.split("/");
			for (String sUnit : sArr) {
				brandSet.add(sUnit.toLowerCase());
			}
		}
		String brandName = (String) task.get("brandName");
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

	private void doCollect(JSONObject dataObject, TaskWritable task) {
		JSONObject gObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);

		JSONArray tArray = new JSONArray();
		tArray.put("BrandConfigVo");
		tArray.put("BrandShopDto");
		JSONUtils.put(argsObject, "target", tArray);
		JSONUtils.put(gObject, "args", argsObject);

		JSONUtils.put(gObject, "rs", dataObject.toString());
		System.err.println("data:" + dataObject);
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}

	private static class ShopVo {
		private Integer siteId = SITE_ID;
		private String brandCode;
		private String brandName;
		private String brandUrl;
		private String synonyms;
		private String region;
		// private Date createTime;
		// private Date updateTime;
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
