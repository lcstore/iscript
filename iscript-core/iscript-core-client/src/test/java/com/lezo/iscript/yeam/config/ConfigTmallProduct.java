package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
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
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.scope.ScriptableUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigTmallProduct implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static final String EMTPY_RESULT = new JSONObject().toString();
	public static final int SITE_ID = 1013;

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject itemObject = getDataObject(task);
		doCollect(itemObject, task);
		return EMTPY_RESULT;
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

	/**
	 * {"dataList":[],"nextList":[]}
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private JSONObject getDataObject(TaskWritable task) throws Exception {
		String url = task.get("url").toString();
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html);
		ResultBean rsBean = new ResultBean();
		ProductBean tBean = new ProductBean();
		rsBean.getDataList().add(tBean);
		JSONObject oConfig = getConfigObject(dom);
		JSONObject itemDO = JSONUtils.getJSONObject(oConfig, "itemDO");
		tBean.setProductCode(JSONUtils.getString(itemDO, "itemId"));
		tBean.setProductName(JSONUtils.getString(itemDO, "title"));
		tBean.setProductBrand(JSONUtils.getString(itemDO, "brand"));
		tBean.setMarketPrice(JSONUtils.getFloat(itemDO, "reservePrice"));
		tBean.setProductUrl(String.format("http://detail.tmall.com/item.htm?id=%s", tBean.getProductCode()));
		Boolean hasSku = JSONUtils.get(itemDO, "hasSku");
		if (hasSku) {
			tBean.setStockNum(1);
		} else {
			tBean.setStockNum(0);
		}
		Elements modelEls = dom.select("#J_AttrUL li[title]:contains(货号)");
		if (!modelEls.isEmpty()) {
			String sModel = modelEls.first().attr("title");
			sModel = sModel.replace(" ", "");
			tBean.setProductModel(sModel.trim());
		}

		Elements imgEls = dom.select("#J_UlThumb li a img[src]");
		if (!imgEls.isEmpty()) {
			String sImgUrl = imgEls.first().attr("src");
			String sMark = "jpg_";
			int index = sImgUrl.indexOf(sMark);
			if (index > 0 && (index + sMark.length() < sImgUrl.length())) {
				sImgUrl = sImgUrl.substring(0, index + sMark.length() - 1);
			}
			tBean.setImgUrl(sImgUrl);
		}

		tBean.setProductBrand(ascii2native(tBean.getProductBrand()));
		Elements destEls = dom.select("#shopExtra a.slogo-shopname[href]");
		if (!destEls.isEmpty()) {
			tBean.setShopUrl(destEls.first().absUrl("href"));
			tBean.setShopName(destEls.first().text().trim());
			String sShopCode = JSONUtils.getString(oConfig, "rstShopId");
			tBean.setShopCode(sShopCode);
		}

		String initUrl = JSONUtils.getString(oConfig, "initApi");
		initUrl += ("&callback=setMdskip&areaId=310100&timestamp=" + System.currentTimeMillis());
		get = new HttpGet(initUrl);
		get.addHeader("Referer", url);
		html = HttpClientUtils.getContent(client, get);
		String sMark = "setMdskip(";
		int fromIndex = html.indexOf(sMark);
		int toIndex = html.lastIndexOf(")");
		html = html.substring(fromIndex + sMark.length(), toIndex);
		JSONObject oMdskip = JSONUtils.getJSONObject(html);
		// .defaultModel.itemPriceResultDO.priceInfo["68909117510"].suggestivePromotionList[0].price
		String keyLink = "defaultModel.itemPriceResultDO.priceInfo";
		String[] keyStrings = keyLink.split("\\.");
		JSONObject priceInfo = getObjectByLink(oMdskip, keyStrings);
		JSONObject oSkuObject = JSONUtils.getJSONObject(priceInfo, priceInfo.keys().next().toString());
		JSONArray promotionList = JSONUtils.get(oSkuObject, "promotionList");
		JSONObject oPromotObject = promotionList.getJSONObject(0);
		tBean.setProductPrice(JSONUtils.getFloat(oPromotObject, "price"));

		JSONObject sellCountDO = getObjectByLink(oMdskip, "defaultModel.sellCountDO".split("\\."));
		tBean.setSoldNum(JSONUtils.getInteger(sellCountDO, "sellCount"));
		System.err.println(oPromotObject.toString());
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	public static String ascii2native(String ascii) {
		int n = ascii.length() / 6;
		Pattern oReg = Pattern.compile("[0-9]{5}");
		StringBuilder sb = new StringBuilder(n);
		Matcher matcher = oReg.matcher(ascii);
		while (matcher.find()) {
			String code = matcher.group();
			char ch = (char) Integer.parseInt(code);
			sb.append(ch);
		}
		return sb.toString();
	}

	private <T> T getObjectByLink(JSONObject souceObject, String[] keyArray) {
		JSONObject destObject = souceObject;
		int len = keyArray.length - 1;
		for (int i = 0; i < len; i++) {
			destObject = JSONUtils.getJSONObject(destObject, keyArray[i]);
		}
		return JSONUtils.get(destObject, keyArray[len]);
	}

	private JSONObject getConfigObject(Document dom) {
		Elements scriptEls = dom.select("div#J_DetailMeta script");
		String script = null;
		for (Element sEle : scriptEls) {
			String content = sEle.html();
			if (content.indexOf("TShop.Setup") > 0) {
				script = content;
				break;
			}
		}
		String source = "var oConfig={}; var TShop={}; TShop.poc=function(data){}; TShop.setConfig=function(data){}; TShop.Setup=function(data){oConfig=data;}; ";
		source += " " + script;
		source += "; var sConfig = JSON.stringify(oConfig);";
		Context cx = Context.enter();
		Scriptable coreScriptable = ScriptableUtils.getCoreScriptable();
		ScriptableObject scope = (ScriptableObject) cx.initStandardObjects((ScriptableObject) coreScriptable);
		cx.evaluateString(scope, source, "<cmd>", 0, null);

		String sConfig = Context.toString(ScriptableObject.getProperty(scope, "sConfig"));
		return JSONUtils.getJSONObject(sConfig);
	}

	private class ProductBean {
		// productStat
		private String productCode;
		private String productName;
		private String productUrl;
		private Float productPrice;
		private Float marketPrice;
		private Integer soldNum;
		private Integer commentNum;
		private Integer stockNum;
		private String categoryNav;
		// product
		private String productBrand;
		private String productModel;
		private String productAttr;
		private String barCode;
		private String imgUrl;
		private Date onsailTime;

		private Integer siteId = SITE_ID;
		private Integer goodComment;
		private Integer poorComment;

		// shopDto
		private Integer shopId;
		private String shopName;
		private String shopCode;
		private String shopUrl;

		public String getProductCode() {
			return productCode;
		}

		public void setProductCode(String productCode) {
			this.productCode = productCode;
		}

		public String getProductName() {
			return productName;
		}

		public void setProductName(String productName) {
			this.productName = productName;
		}

		public String getProductUrl() {
			return productUrl;
		}

		public void setProductUrl(String productUrl) {
			this.productUrl = productUrl;
		}

		public Float getProductPrice() {
			return productPrice;
		}

		public void setProductPrice(Float productPrice) {
			this.productPrice = productPrice;
		}

		public Float getMarketPrice() {
			return marketPrice;
		}

		public void setMarketPrice(Float marketPrice) {
			this.marketPrice = marketPrice;
		}

		public Integer getSoldNum() {
			return soldNum;
		}

		public void setSoldNum(Integer soldNum) {
			this.soldNum = soldNum;
		}

		public Integer getCommentNum() {
			return commentNum;
		}

		public void setCommentNum(Integer commentNum) {
			this.commentNum = commentNum;
		}

		public Integer getStockNum() {
			return stockNum;
		}

		public void setStockNum(Integer stockNum) {
			this.stockNum = stockNum;
		}

		public String getCategoryNav() {
			return categoryNav;
		}

		public void setCategoryNav(String categoryNav) {
			this.categoryNav = categoryNav;
		}

		public String getProductBrand() {
			return productBrand;
		}

		public void setProductBrand(String productBrand) {
			this.productBrand = productBrand;
		}

		public String getProductModel() {
			return productModel;
		}

		public void setProductModel(String productModel) {
			this.productModel = productModel;
		}

		public String getProductAttr() {
			return productAttr;
		}

		public void setProductAttr(String productAttr) {
			this.productAttr = productAttr;
		}

		public String getBarCode() {
			return barCode;
		}

		public void setBarCode(String barCode) {
			this.barCode = barCode;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public Date getOnsailTime() {
			return onsailTime;
		}

		public void setOnsailTime(Date onsailTime) {
			this.onsailTime = onsailTime;
		}

		public Integer getSiteId() {
			return siteId;
		}

		public void setSiteId(Integer siteId) {
			this.siteId = siteId;
		}

		public Integer getGoodComment() {
			return goodComment;
		}

		public void setGoodComment(Integer goodComment) {
			this.goodComment = goodComment;
		}

		public Integer getPoorComment() {
			return poorComment;
		}

		public void setPoorComment(Integer poorComment) {
			this.poorComment = poorComment;
		}

		public String getShopName() {
			return shopName;
		}

		public void setShopName(String shopName) {
			this.shopName = shopName;
		}

		public String getShopCode() {
			return shopCode;
		}

		public void setShopCode(String shopCode) {
			this.shopCode = shopCode;
		}

		public String getShopUrl() {
			return shopUrl;
		}

		public void setShopUrl(String shopUrl) {
			this.shopUrl = shopUrl;
		}

		public Integer getShopId() {
			return shopId;
		}

		public void setShopId(Integer shopId) {
			this.shopId = shopId;
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