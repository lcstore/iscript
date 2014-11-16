package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.URLUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigYhdPromotion implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static final String EMTPY_RESULT = new JSONObject().toString();
	private static Map<String, String> hostIpMap = new HashMap<String, String>();
	static {
		hostIpMap.put("item.yhd.com", "180.153.252.38");
		hostIpMap.put("gps.yihaodian.com", "180.153.252.46");
		hostIpMap.put("e.yhd.com", "180.153.252.36");
		hostIpMap.put("item-home.yhd.com", "180.153.252.38");
	}

	private HttpGet createHttpGetWithIp(String url) throws Exception {
		URI oUri = new URI(url);
		String host = oUri.getHost();
		String oldUrl = oUri.toString();
		String ip = hostIpMap.get(host);
		if (ip != null) {
			url = oldUrl.replace(host, ip);
		}
		HttpGet get = new HttpGet(url);
		get.addHeader("Host", oUri.getHost());
		return get;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		addCookie();
		JSONObject itemObject = getDataObject(task);
		doCollect(itemObject, task);
		return EMTPY_RESULT;
	}

	private void doCollect(JSONObject dataObject, TaskWritable task) {
		JSONObject gObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);
		JSONUtils.put(argsObject, "target", "PromotionMapDto");

		JSONUtils.put(gObject, "args", argsObject);

		JSONUtils.put(gObject, "rs", dataObject.toString());
		System.err.println(dataObject);
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}

	private void addCookie() {
		BasicClientCookie cookie = new BasicClientCookie("__utma", "40580330.1541470702.1396602044.1406527175.1406603327.18");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmc", "193324902");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmz", "193324902.1401026096.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("provinceId", "1");
		client.getCookieStore().addCookie(cookie);
		String[] uArr = UUID.randomUUID().toString().split("-");
		cookie = new BasicClientCookie("uname", uArr[0]);
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("yihaodian_uid", "" + Math.abs(uArr[0].hashCode()));
		client.getCookieStore().addCookie(cookie);
	}

	/**
	 * {"data":[],"nexts":[]}
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private JSONObject getDataObject(TaskWritable task) throws Exception {
		List<PromotionBean> promotList = getPromotions(task);
		if (!promotList.isEmpty()) {
			ResultBean rsBean = new ResultBean();
			rsBean.getDataList().addAll(promotList);
			ObjectMapper mapper = new ObjectMapper();
			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, rsBean);
			return new JSONObject(writer.toString());
		}
		return new JSONObject();
	}

	private List<PromotionBean> getPromotions(TaskWritable task) throws Exception {
		// http://item-home.yhd.com/item/ajax/ajaxProductPromotion.do?callback=detailPromotion.reduceScrollbar&productID=9122034&merchantID=1&productMercantID=10323264&categoryId=18226&brandId=585&isYihaodian=1&vId=bpGHKnpNaLAfegw3yM1a8w%253D%253D&version=version_new
		List<PromotionBean> promotionList = new ArrayList<ConfigYhdPromotion.PromotionBean>();
		String url = (String) task.get("url");
		url = doParamEncode(url);
		HttpGet get = createHttpGetWithIp(url);
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html);
		if (isHome(dom)) {
			String pCode = URLUtils.getCodeFromUrl(url);
			PromotionBean bean = new PromotionBean();
			bean.setProductCode(pCode);
			bean.setPromoteStatus(PromotionBean.PROMOTE_STATUS_END);
			promotionList.add(bean);
			return promotionList;
		}
		Scriptable scope = getPromotScope(dom);
		String result = Context.toString(ScriptableObject.getProperty(scope, "dataString"));
		String productCode = Context.toString(ScriptableObject.getProperty(scope, "b"));
		Document promotDom = Jsoup.parse(result);
		Elements promotEls = promotDom.select("div.sp_item");
		if (promotEls.isEmpty()) {
			PromotionBean bean = new PromotionBean();
			bean.setProductCode(productCode);
			bean.setPromoteStatus(PromotionBean.PROMOTE_STATUS_END);
			promotionList.add(bean);
			return promotionList;
		}
		for (Element ele : promotEls) {
			Elements urlEls = ele.select("p.sp_txt a[href]");
			if (!urlEls.isEmpty()) {
				String pUrl = urlEls.first().absUrl("href");
				Pattern codeReg = Pattern.compile("(.*?-pt)([0-9]{4,})(-.*)");
				Matcher matcher = codeReg.matcher(pUrl);
				if (matcher.find()) {
					PromotionBean bean = new PromotionBean();
					bean.setProductCode(productCode);
					bean.setPromoteUrl(pUrl);
					bean.setPromoteCode(matcher.group(2));
					bean.setPromoteStatus(PromotionBean.PROMOTE_STATUS_START);
					bean.setSiteId(1002);
					bean.setPromoteName(ele.select("div.dt").first().ownText());
					bean.setPromoteType(PromotionBean.PROMOTE_TYPE_UNKONW);
					bean.setPromoteDetail(urlEls.first().text());
					promotionList.add(bean);
				}
			}
		}
		promotEls = null;
		dom = null;
		scope = null;
		return promotionList;
	}

	private Scriptable getPromotScope(Document dom) throws Exception {
		Elements scriptEls = dom.select("#headerNav ~ script");
		String script = null;
		for (Element ele : scriptEls) {
			String html = ele.html();
			if (html.indexOf("detailparams") > 0) {
				script = html;
				break;
			}
		}
		scriptEls = null;
		String productId = dom.select("#productId").val();
		String merchantId = dom.select("#merchantId").val();
		String productMercantId = dom.select("#productMercantId").val();
		String categoryId = dom.select("#categoryId").val();
		String brandID = dom.select("#brandID").val();
		String isYiHaoDian = dom.select("#isYiHaoDian").val();
		Context cx = Context.enter();
		try {
			Scriptable scope = cx.initStandardObjects(null);
			ScriptableObject.putProperty(scope, "c", productId);
			ScriptableObject.putProperty(scope, "b", productMercantId);
			ScriptableObject.putProperty(scope, "d", categoryId);
			ScriptableObject.putProperty(scope, "e", brandID);
			ScriptableObject.putProperty(scope, "f", merchantId);
			ScriptableObject.putProperty(scope, "isYhd", isYiHaoDian);
			StringBuilder sb = new StringBuilder();
			sb.append("var document={};document.body={};var screen={};screen.width=1240;");
			sb.append(script);
			sb.append("var promotUrl=detailPath.ctxDomain+\"/item/ajax/ajaxProductPromotion.do?callback=detailPromotion.reduceScrollbar&productID=\"+c+\"&merchantID=\"+f+\"&productMercantID=\"+b+\"&categoryId=\"+d+\"&brandId=\"+e+\"&isYihaodian=\"+isYhd+\"&vId=\"+encodeURIComponent(encodeURIComponent(detailparams.paramSignature))+\"&version=version_new\";+\"&uid=\"+new Date().getTime();");

			String source = sb.toString();
			cx.evaluateString(scope, source, "<cmd>", 0, null);
			String promotUrl = Context.toString(ScriptableObject.getProperty(scope, "promotUrl"));
			HttpGet get = createHttpGetWithIp(promotUrl);
			get.addHeader("Referer", dom.baseUri());
			String html = HttpClientUtils.getContent(client, get);
			html = html.replace("detailPromotion.reduceScrollbar", "var dataString = getCallBack");
			source = "function getCallBack(oData){return oData.value;}; ";
			source += html;
			cx.evaluateString(scope, source, "<cmd>", 0, null);
			return scope;
		} finally {
			Context.exit();
		}
	}

	private String doParamEncode(String url) throws Exception {
		int index = url.indexOf("?");
		if (index > 0) {
			String paramUrl = url.substring(index + 1);
			url = url.substring(0, index + 1) + URLEncoder.encode(paramUrl, "UTF-8");
		}
		return url;
	}

	private boolean isHome(Document dom) {
		Elements navEls = dom.select("#comParamId div.crumb a[href]");
		return navEls.isEmpty();
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

		private Integer siteId = 1002;
		private String productCode;
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

		public Integer getSiteId() {
			return siteId;
		}

		public void setSiteId(Integer siteId) {
			this.siteId = siteId;
		}

		public String getProductCode() {
			return productCode;
		}

		public void setProductCode(String productCode) {
			this.productCode = productCode;
		}
	}

	public class HttpDirector {
		private DefaultHttpClient client;

		public HttpDirector(DefaultHttpClient client) {
			super();
			this.client = client;
		}

		public String get(String url) throws Exception {
			HttpGet get = createHttpGetWithIp(url);
			return HttpClientUtils.getContent(client, get);
		}

		public String get(String url, Object args) throws Exception {
			HttpGet get = createHttpGetWithIp(url);
			return HttpClientUtils.getContent(client, get);
		}
	}

	interface ScopeCallBack {
		void doCallBack(ScriptableObject scope, Object targetObject);
	}

	final class ResultBean {
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