package com.lezo.iscript.yeam.config;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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

public class ConfigJdPromotion implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static final String EMTPY_RESULT = new JSONObject().toString();
	private static String promotionScript = null;

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

		JSONUtils.put(gObject, "args", argsObject);

		JSONUtils.put(gObject, "rs", dataObject.toString());

		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}

	/**
	 * {"data":[],"nexts":[]}
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private JSONObject getDataObject(TaskWritable task) throws Exception {
		JSONObject itemObject = new JSONObject();
		List<PromotionBean> promotList = getPromotions(task);
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, promotList);
		System.out.println("writer" + writer);
		return itemObject;
	}

	private String getPromotionScript(String url) {
		if (promotionScript == null) {
			synchronized (ConfigJdPromotion.class) {
				if (promotionScript == null) {
					String promotUrl = "http://misc.360buyimg.com/product/js/2012/promotion.js";
					HttpGet get = new HttpGet(promotUrl);
					get.addHeader("Referer", url);
					get.addHeader("Accept", "application/javascript, */*;q=0.8");
					get.addHeader("Accept-Encoding", "gzip, deflate");
					get.addHeader("Accept-Language", "zh-CN");
					try {
						promotionScript = HttpClientUtils.getContent(client, get, "GBK");
					} catch (Exception e) {
						e.printStackTrace();
						if (!get.isAborted()) {
							get.abort();
						}
					}
				}
			}
		}
		return promotionScript;
	}

	private List<PromotionBean> getPromotions(TaskWritable task) throws Exception {
		// http://pi.3.cn/promoinfo/get?id=1095329&area=1_0&origin=1&callback=Promotions.set
		List<PromotionBean> promotionList = new ArrayList<ConfigJdPromotion.PromotionBean>();
		String url = (String) task.get("url");
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html);
		JSONObject oPromotData = createPromotionDocument(dom, task);
		System.out.println(oPromotData);
		Elements promotAs = dom.select("#product-promotions");
		if (!promotAs.isEmpty()) {
			JSONArray promotArray = JSONUtils.get(oPromotData, "promotionInfoList");
			Element promot = promotAs.first();
			int size = promot.children().size();
			PromotionBean bean = new PromotionBean();
			for (int i = 0; i < size; i++) {
				Element child = promot.child(i);
				if ("br".equals(child.tagName())) {
					JSONObject itemObject = promotArray.getJSONObject(promotionList.size());
					addPromote(promotionList, bean, itemObject);
					bean = new PromotionBean();
				} else if ("em".equals(child.tagName()) && child.hasClass("hl_red_bg")) {
					bean.setPromoteName(child.ownText().trim());
				} else if ("em".equals(child.tagName()) && child.hasClass("hl_red")) {
					String pDetail = bean.getPromoteDetail();
					if (pDetail == null) {
						pDetail = child.ownText().trim();
					} else {
						pDetail += child.ownText().trim();
					}
					bean.setPromoteDetail(pDetail);
				} else if ("a".equals(child.tagName()) && child.hasAttr("href")) {
					String actUrl = child.absUrl("href");
					if (actUrl != null && actUrl.indexOf("jd.com/act/") > 0) {
						bean.setPromoteUrl(actUrl);
					}
				}
			}
			JSONObject itemObject = promotArray.getJSONObject(promotionList.size());
			addPromote(promotionList, bean, itemObject);
		}
		return promotionList;
	}

	private void addPromote(List<PromotionBean> promotionList, PromotionBean bean, JSONObject itemObject) throws Exception {
		Long endTimeMillis = JSONUtils.getLong(itemObject, "promoEndTime");
		if (endTimeMillis != null && endTimeMillis > 0 && endTimeMillis <= System.currentTimeMillis()) {
			bean.setPromoteStatus(PromotionBean.PROMOTE_STATUS_END);
		}
		String adwordUrl = JSONUtils.getString(itemObject, "adwordUrl");
		if (!StringUtils.isEmpty(adwordUrl)) {
			bean.setPromoteUrl(adwordUrl);
		}
		JSONArray ladderArray = JSONUtils.get(itemObject, "fullLadderDiscountList");
		if (ladderArray != null && ladderArray.length() > 0) {
			JSONArray conditionArray = new JSONArray();
			int size = ladderArray.length();
			for (int i = 0; i < size; i++) {
				JSONObject dataObject = ladderArray.getJSONObject(i);
				addCondition(conditionArray, dataObject);
			}
			if (conditionArray.length() > 0) {
				bean.setPromoteNums(conditionArray.toString());
			}
		} else {
			JSONArray conditionArray = new JSONArray();
			addCondition(conditionArray, itemObject);
			if (conditionArray.length() > 0) {
				bean.setPromoteNums(conditionArray.toString());
			}
		}
		JSONArray adwordGiftSkuList = JSONUtils.get(itemObject, "adwordGiftSkuList");
		if (adwordGiftSkuList != null && adwordGiftSkuList.length() > 0) {
			bean.setPromoteExtra(adwordGiftSkuList.toString());
		}
		bean.setPromoteCode(JSONUtils.getString(itemObject, "promoId"));
		promotionList.add(bean);
	}

	private void addCondition(JSONArray conditionArray, JSONObject dataObject) {
		JSONObject cObject = new JSONObject();
		addFloatIfAbove(cObject, dataObject, "needMoney", 0F);
		addFloatIfAbove(cObject, dataObject, "addMoney", 0F);
		addFloatIfAbove(cObject, dataObject, "rewardMoney", 0F);
		addIntIfAbove(cObject, dataObject, "needNum", 0);
		addIntIfAbove(cObject, dataObject, "minNum", 0);
		addIntIfAbove(cObject, dataObject, "maxNum", 0);
		if (cObject.length() > 0) {
			conditionArray.put(cObject);
		}
	}

	private void addFloatIfAbove(JSONObject cObject, JSONObject dataObject, String key, Float aboveValue) {
		Float valueNum = JSONUtils.getFloat(dataObject, key);
		if (valueNum != null && valueNum > aboveValue) {
			JSONUtils.put(cObject, key, valueNum);
		}
	}

	private void addIntIfAbove(JSONObject cObject, JSONObject dataObject, String key, Integer aboveValue) {
		Integer valueNum = JSONUtils.getInteger(dataObject, key);
		if (valueNum != null && valueNum > aboveValue) {
			JSONUtils.put(cObject, key, valueNum);
		}
	}

	private JSONObject createPromotionDocument(Document dom, TaskWritable task) throws IOException {
		try {
//			String source = FileUtils.readFileToString(new File("src/test/resources/bcore.js"), "UTF-8");
			String source = "//debug.canDebug=false;\r\nwindow = {};\r\nvar oEles = $('head script');\r\nvar len = oEles.length;\r\nfor (var i = 0; i < len; i++) {\r\n	var script = oEles.get(i).html();\r\n	if (script.indexOf('window.pageConfig') >= 0) {\r\n		eval('' + script);\r\n		break;\r\n	}\r\n}\r\ntoGlobal(window);\r\nvar oGlobal = {};\r\noGlobal.cat = pageConfig.product.cat;\r\noGlobal.getNewUserLevel = function(t) {\r\n	switch (t) {\r\n	case 50:\r\n		return \"注册用户\";\r\n	case 56:\r\n		return \"铜牌用户\";\r\n	case 59:\r\n		return \"注册用户\";\r\n	case 60:\r\n		return \"银牌用户\";\r\n	case 61:\r\n		return \"银牌用户\";\r\n	case 62:\r\n		return \"金牌用户\";\r\n	case 63:\r\n		return \"钻石用户\";\r\n	case 64:\r\n		return \"经销商\";\r\n	case 110:\r\n		return \"VIP\";\r\n	case 66:\r\n		return \"京东员工\";\r\n	case -1:\r\n		return \"未注册\";\r\n	case 88:\r\n		return \"钻石用户\";\r\n	case 90:\r\n		return \"企业用户\";\r\n	case 103:\r\n		return \"钻石用户\";\r\n	case 104:\r\n		return \"钻石用户\";\r\n	case 105:\r\n		return \"钻石用户\"\r\n	}\r\n	return \"未知\";\r\n}\r\nfunction readCookie(key) {\r\n	return '1-72-4137-0';\r\n}\r\n\r\ntry {\r\n	String.prototype.process = function(oPromot, b) {\r\n		var res = '';\r\n		if (oPromot && oPromot.adwordGiftSkuList) {\r\n			var adwordGiftSkuList = oPromot.adwordGiftSkuList;\r\n			var len = adwordGiftSkuList.length;\r\n			for (var i = 0; i < len; i++) {\r\n				var item = adwordGiftSkuList[i];\r\n				if (item.giftType == 2) {\r\n					res += '<div class=\"li-img\"><a target=\"_blank\" href=\"http://item.jd.com/'\r\n							+ item.skuId + '.html\">';\r\n					if (item.imagePath !== \"\") {\r\n						res += '<img src=\"http://img11.360buyimg.com/n5/'\r\n								+ item.imagePath + ' width=\"25\" height=\"25\" />';\r\n					} else {\r\n						res += '<img src=\"http://misc.360buyimg.com/product/skin/2012/i/gift.png\" width=\"25\" height=\"25\" />';\r\n					}\r\n					res += item.name;\r\n					res += '</a><em class=\"hl_red\"> ×' + item.number\r\n							+ '</em></div>\\n';\r\n				}\r\n			}\r\n		}\r\n		debug.log(this);\r\n		debug.log('ddd:' + res);\r\n		return res;\r\n	}\r\n} catch (e) {\r\n}";
//			source += "var G = oGlobal;";
			source += getPromotionScript(dom.baseUri());
			source += "var oldPromotions = Promotions; Promotions={};";
			source += "var sPromotData; Promotions.set =function(result){sPromotData=JSON.stringify(result);oldPromotions.set(result);};";
			source += "var G = oGlobal;  oldPromotions.init(G.sku);";
			source += "debug.log($('#product-promotions').html()); debug.log($('#summary-gifts').html());";
			String argsString = "args=" + JSONUtils.getJSONObject(task.getArgs());
			Context cx = Context.enter();
			Scriptable coreScriptable = ScriptableUtils.getCoreScriptable();
			ScriptableObject scope = null;
			scope = (ScriptableObject) cx.initStandardObjects((ScriptableObject) coreScriptable);
			cx.evaluateString(scope, argsString, "<args>", 0, null);
			ScriptableObject.putProperty(scope, "$document", dom);
			ScriptableObject.putProperty(scope, "http", new HttpDirector(client));
			cx.evaluateString(scope, source, "<cmd>", 0, null);
			String sPromotData = Context.toString(ScriptableObject.getProperty(scope, "sPromotData"));
			return JSONUtils.getJSONObject(sPromotData);
		} finally {
			Context.exit();
		}
	}

	private class PromotionBean {
		/**
		 * 状态，-1-促销未开始,0-促销中，1-促销结束
		 */
		public static final int PROMOTE_STATUS_WAIT = -1;
		public static final int PROMOTE_STATUS_START = 0;
		public static final int PROMOTE_STATUS_END = 1;
		/**
		 * 促销类型，-1-未知，0-满减,1-满赠，2-满折
		 */
		public static final int PROMOTE_TYPE_UNKONW = -1;
		public static final int PROMOTE_TYPE_FULL_SUB = 0;
		public static final int PROMOTE_TYPE_FULL_GIFT = 1;
		public static final int PROMOTE_TYPE_FULL_REBATE = 2;

		private String promoteCode;
		private String promoteName;
		private String promoteDetail;
		private String promoteExtra;
		private String promoteNums;
		private String promoteUrl;
		private Integer promoteType = PROMOTE_TYPE_UNKONW;
		private Integer promoteStatus = PROMOTE_STATUS_START;

		public String getPromoteCode() {
			return promoteCode;
		}

		public void setPromoteCode(String promoteCode) {
			this.promoteCode = promoteCode;
		}

		public String getPromoteName() {
			return promoteName;
		}

		public void setPromoteName(String promoteName) {
			this.promoteName = promoteName;
		}

		public String getPromoteDetail() {
			return promoteDetail;
		}

		public void setPromoteDetail(String promoteDetail) {
			this.promoteDetail = promoteDetail;
		}

		public String getPromoteNums() {
			return promoteNums;
		}

		public void setPromoteNums(String promoteNums) {
			this.promoteNums = promoteNums;
		}

		public String getPromoteUrl() {
			return promoteUrl;
		}

		public void setPromoteUrl(String promoteUrl) {
			this.promoteUrl = promoteUrl;
		}

		public Integer getPromoteType() {
			return promoteType;
		}

		public void setPromoteType(Integer promoteType) {
			this.promoteType = promoteType;
		}

		public Integer getPromoteStatus() {
			return promoteStatus;
		}

		public void setPromoteStatus(Integer promoteStatus) {
			this.promoteStatus = promoteStatus;
		}

		public String getPromoteExtra() {
			return promoteExtra;
		}

		public void setPromoteExtra(String promoteExtra) {
			this.promoteExtra = promoteExtra;
		}
	}

	public class HttpDirector {
		private DefaultHttpClient client;

		public HttpDirector(DefaultHttpClient client) {
			super();
			this.client = client;
		}

		public String get(String url) throws Exception {
			HttpGet get = new HttpGet(url);
			return HttpClientUtils.getContent(client, get);
		}

		public String get(String url, Object args) throws Exception {
			HttpGet get = new HttpGet(url);
			return HttpClientUtils.getContent(client, get);
		}
	}

	interface ScopeCallBack {
		void doCallBack(ScriptableObject scope, Object targetObject);
	}

	private void evaluateString(String source, ScopeCallBack callBack, Object targetObject) {
		try {
			Context cx = Context.enter();
			ScriptableObject scope = cx.initStandardObjects();
			cx.evaluateString(scope, source, "<cmd>", 0, null);
			callBack.doCallBack(scope, targetObject);
		} finally {
			Context.exit();
		}
	}
}