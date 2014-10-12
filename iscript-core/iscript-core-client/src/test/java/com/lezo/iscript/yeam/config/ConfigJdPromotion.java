package com.lezo.iscript.yeam.config;

import java.io.File;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.ConfigJdProduct.TargetBean;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigJdPromotion implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static final String EMTPY_RESULT = new JSONObject().toString();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static int[] stockArr = { 0, -1, 1 };

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
		String url = task.get("url").toString();
		JSONObject itemObject = new JSONObject();
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html, url);
		Elements scriptAs = dom.select("script[type]");
		PromotionBean tBean = new PromotionBean();
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, tBean);
		System.out.println(writer);
		return itemObject;
	}

	private void addPromotions(TargetBean tBean, Document dom, TaskWritable task) throws Exception {
		// http://pi.3.cn/promoinfo/get?id=1095329&area=1_0&origin=1&callback=Promotions.set
		String mUrl = String.format("http://pi.3.cn/promoinfo/get?id=%s&area=1_0&origin=1&callback=Promotions.set", tBean.getProductCode());
		HttpGet get = new HttpGet(mUrl);
		String html = HttpClientUtils.getContent(client, get);
		html = html.replace("Promotions.set", "var oPromotList= callback");
		html = "function callback(data){return data.promotionInfoList;}; " + html;
		evaluateString(html, new ScopeCallBack() {
			@Override
			public void doCallBack(ScriptableObject scope, Object targetObject) {
				TargetBean tBean = (TargetBean) targetObject;
				Scriptable oPromotArray = (Scriptable) ScriptableObject.getProperty(scope, "oPromotList");
				int index = 0;
				// List<PromotionBean> promotionList = tBean.getPromotionList();
				// while (oPromotArray.has(index, oPromotArray)) {
				// Scriptable oPromot = (Scriptable) oPromotArray.get(index,
				// oPromotArray);
				// PromotionBean promote = new PromotionBean();
				// promote.setPromoteCode(Context.toString(ScriptableObject.getProperty(oPromot,
				// "promoId")));
				// promote.setPromoteUrl(Context.toString(ScriptableObject.getProperty(oPromot,
				// "adwordUrl")));
				// promotionList.add(promote);
				// index++;
				// }
			}
		}, tBean);
		Elements scriptAs = dom.select("div.breadcrumb > script");

		String source = FileUtils.readFileToString(new File("src/test/resources/jd.js"));
		try {
			String argsString = "args=" + JSONUtils.getJSONObject(task.getArgs());
			Context cx = Context.enter();
			ScriptableObject scope = cx.initStandardObjects();
			cx.evaluateString(scope, argsString, "<args>", 0, null);
			ScriptableObject.putProperty(scope, "src", dom.select("body").first());
			ScriptableObject.putProperty(scope, "http", new HttpDirector(client));
			cx.evaluateString(scope, source, "<cmd>", 0, null);
			Object stockObject = ScriptableObject.getProperty(scope, "promotionInfo");
		} finally {
			Context.exit();
		}

	}

	class PromotionBean {
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
		private String promoteNums;
		private String promoteUrl;
		private Integer promoteType = PROMOTE_STATUS_START;
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
