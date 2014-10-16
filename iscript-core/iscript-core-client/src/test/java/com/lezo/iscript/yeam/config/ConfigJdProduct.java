package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigJdProduct implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static final String EMTPY_RESULT = new JSONObject().toString();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static int[] stockArr = { 0, -1, 1 };
	private static Pattern oBarCodeReg = Pattern.compile("条形码[\\s]*([0-9]{13,})");
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
		JSONObject itemObject = new JSONObject();
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html, url);
		if (isHome(dom)) {
			return itemObject;
		}
		Elements scriptAs = dom.select("script[type]");
		TargetBean tBean = new TargetBean();
		if (!scriptAs.isEmpty()) {
			String sMark = "";
			String destScript = null;
			int size = scriptAs.size();
			for (int i = 0; i < size; i++) {
				String script = scriptAs.get(i).html();
				if (script.indexOf(sMark) >= 0) {
					destScript = script;
					break;
				}
			}
			if (destScript != null) {
				destScript = "var window={}; " + destScript;
				destScript += " var skuid=window.pageConfig.product.skuid;";
				destScript += " var name=window.pageConfig.product.name;";
				evaluateString(destScript, new ScopeCallBack() {
					@Override
					public void doCallBack(ScriptableObject scope, Object targetObject) {
						TargetBean tBean = (TargetBean) targetObject;
						tBean.setProductCode(ScriptableObject.getProperty(scope, "skuid").toString());
						tBean.setProductName(ScriptableObject.getProperty(scope, "name").toString());
					}
				}, tBean);
				tBean.setProductUrl(url);
				addPrice(tBean, dom);
				addAttributes(tBean, dom);
				addComment(tBean, dom);
				addBarCode(tBean, dom);
				addStock(tBean, dom, task);
			}
		}
		ResultBean rsBean = new ResultBean();
		rsBean.getDataList().add(tBean);
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	private void addBarCode(TargetBean tBean, Document dom) {
		String name = tBean.getProductName();
		if(!StringUtils.isEmpty(name)){
			Matcher matcher = oBarCodeReg.matcher(name);
			if(matcher.find()){
				tBean.setBarCode(matcher.group(1));
			}
		}
		
	}

	private boolean isHome(Document dom) {
		return !dom.select("li#nav-home.curr a:contains(首页)").isEmpty();
	}

	private void addStock(TargetBean tBean, Document dom, TaskWritable task) throws Exception {
		// a./--// b."--\" c.\r\n mark to string \r\n
		String source = "var debug = {};\r\ndebug.log = function(msg) {\r\n	if (msg) {\r\n		java.lang.System.out.println(msg);\r\n	} else {\r\n		java.lang.System.out.println(\"NULL OR Undefine.\");\r\n	}\r\n}\r\nfunction getCityId() {\r\n	var sCid = '78'\r\n	if (args.regionname == 'BJ') {\r\n		sCid = '72';\r\n	} else if (args.regionname == 'GZ') {\r\n		sCid = '1601';\r\n	} else if (args.regionname == 'WH') {\r\n		sCid = '1381';\r\n	} else if (args.regionname == 'CD') {\r\n		sCid = '1930';\r\n	}\r\n	return sCid;\r\n}\r\nfunction getRegionCodeObj() {\r\n	var oRegion = {};\r\n	var sLocation = '&provinceid=2&cityid=78&areaid=79'\r\n	if (args.regionname == 'BJ') {\r\n		oRegion.provinceid = 1;\r\n		oRegion.cityid = 72;\r\n		oRegion.areaid = 4137;\r\n		oRegion.townid = 0;\r\n		sLocation = '&provinceid=1&cityid=72&areaid=4137&townid=0';\r\n	} else if (args.regionname == 'GZ') {\r\n		oRegion.provinceid = 19;\r\n		oRegion.cityid = 1601;\r\n		oRegion.areaid = 3633;\r\n		oRegion.townid = 35726;\r\n		sLocation = '&provinceid=19&cityid=1601&areaid=3633&townid=35726';\r\n	} else if (args.regionname == 'WH') {\r\n		oRegion.provinceid = 17;\r\n		oRegion.cityid = 1381;\r\n		oRegion.areaid = 3582;\r\n		oRegion.townid = 50739;\r\n		sLocation = '&provinceid=17&cityid=1381&areaid=3582&townid=50739';\r\n	} else if (args.regionname == 'CD') {\r\n		oRegion.provinceid = 22;\r\n		oRegion.cityid = 1930;\r\n		oRegion.areaid = 4284;\r\n		oRegion.townid = 0;\r\n		sLocation = '&provinceid=22&cityid=1930&areaid=4284&townid=0';\r\n	} else {\r\n		oRegion.provinceid = 2;\r\n		oRegion.cityid = 2811;\r\n		oRegion.areaid = 2860;\r\n		oRegion.townid = 0;\r\n		sLocation = '&provinceid=2&cityid=2811&areaid=2860&townid=0'\r\n	}\r\n	return oRegion;\r\n}\r\nfunction getCode() {\r\n	var _url = args.url;\r\n	var codeReg = new RegExp(\"[0-9]{5,}\", \"gm\");\r\n	var match = _url.match(codeReg);\r\n	if (match && match[0]) {\r\n		return match[0];\r\n	}\r\n	return \"\";\r\n}\r\nfunction getJSONObj(sHtml) {\r\n	var oJObjReg = new RegExp('{.*}', 'gm'), oMatch = sHtml.match(oJObjReg);\r\n	var sJObj = (oMatch) ? oMatch[0] : \"{}\";\r\n	return eval('(' + sJObj + ')');\r\n}\r\nfunction getSkuId_new(cid, aid, eleSkuIdKey) {\r\n	if (eleSkuIdKey && eleSkuIdKey.length > 0) {\r\n		var areas = null;\r\n		for (var i = 0, j = eleSkuIdKey.length; i < j; i++) {\r\n			if (eleSkuIdKey[i].area && eleSkuIdKey[i].area[cid + \"\"]) {\r\n				areas = eleSkuIdKey[i].area[cid + \"\"];\r\n				if (areas.length == 0 || areas[0] + \"\" == \"0\") {\r\n					return eleSkuIdKey[i].SkuId;\r\n				} else if (areas.length > 0) {\r\n					for (var a = 0, b = areas.length; a < b; a++) {\r\n						if (areas[a] + \"\" == aid + \"\") {\r\n							return eleSkuIdKey[i].SkuId;\r\n						}\r\n					}\r\n				}\r\n			}\r\n		}\r\n	}\r\n	return 0;\r\n}\r\nfunction chooseSkuToArea(provinceId, cityId, areaId, oConfig) {\r\n	var pageConfig = oConfig.pageConfig;\r\n	var eleSkuIdKey = oConfig.eleSkuIdKey;\r\n	var currentSkuId = (pageConfig && pageConfig.product) ? pageConfig.product.skuid\r\n			: '';\r\n	var currentSkuKey = (pageConfig && pageConfig.product) ? pageConfig.product.skuidkey\r\n			: '';\r\n	var isAreaProduct = (eleSkuIdKey) ? true : false;\r\n	if (isAreaProduct && provinceId > 0 && cityId > 0 && areaId > 0) {\r\n		currentSkuId = 0;\r\n		currentSkuKey = \"\";\r\n		var eleRegion = '';\r\n		if (eleRegion) {\r\n			var provinceCitys = eleRegion[provinceId + \"\"];\r\n			if (provinceCitys && provinceCitys.citys\r\n					&& provinceCitys.citys.length > 0) {\r\n				for (var i = 0, j = provinceCitys.citys.length; i < j; i++) {\r\n					if (provinceCitys.citys[i].IdCity == cityId) {\r\n						currentSkuId = provinceCitys.citys[i].SkuId;\r\n						break;\r\n					}\r\n				}\r\n			}\r\n		} else {\r\n			currentSkuId = getSkuId_new(cityId, areaId, eleSkuIdKey);\r\n		}\r\n		if (eleSkuIdKey && eleSkuIdKey.length > 0) {\r\n			for (var i = 0, j = eleSkuIdKey.length; i < j; i++) {\r\n				if (eleSkuIdKey[i].SkuId == currentSkuId) {\r\n					currentSkuKey = eleSkuIdKey[i].Key;\r\n					break;\r\n				}\r\n			}\r\n		}\r\n	}\r\n	debug.log('currentSkuKey:' + currentSkuKey);\r\n	return [ currentSkuKey, currentSkuId ];\r\n}\r\nfunction getRegionConfig(body, oConfig) {\r\n	var oEleRegion_as = body.select('div#product-intro > script[type]');\r\n	if (oEleRegion_as && oEleRegion_as.first()) {\r\n		var sEleRegion = oEleRegion_as.first().html();\r\n		if (!sEleRegion || '' == sEleRegion) {\r\n			return oConfig;\r\n		}\r\n		eval(' ' + sEleRegion + ' ');\r\n		if (typeof (warestatus) != 'undefined') {\r\n			oConfig.warestatus = warestatus;\r\n		}\r\n		if (typeof (eleSkuIdKey) != 'undefined') {\r\n			oConfig.eleSkuIdKey = eleSkuIdKey;\r\n		}\r\n	}\r\n	return oConfig;\r\n}\r\nfunction getPageConfig(body, oConfig) {\r\n	var head = body.previousElementSibling(), oScript = head\r\n			.select('script[type]');\r\n	for (var i = 0; i < oScript.size(); i++) {\r\n		var sHtml = oScript.get(i).html();\r\n		if (sHtml.contains('pageConfig')) {\r\n			sHtml = sHtml.replace('\\'\\',', '\"\",');\r\n			sHtml = sHtml.replace('\\'\\'', '\"\"');\r\n			sHtml = 'var window = {};' + sHtml;\r\n			eval(' ' + sHtml + ' ');\r\n			if (typeof (window.pageConfig) != 'undefined') {\r\n				oConfig.pageConfig = window.pageConfig;\r\n			}\r\n			break;\r\n		}\r\n	}\r\n	return oConfig;\r\n}\r\nfunction getWareinfo(body, oConfig) {\r\n	var oWareinfo = body.select('#book-price li.sub > script[type]');\r\n	if (oWareinfo.isEmpty()) {\r\n		return oConfig;\r\n	}\r\n	var sWareinfo = oWareinfo.first().html();\r\n	if (!sWareinfo || '' == sWareinfo) {\r\n		return oConfig;\r\n	}\r\n	eval(' ' + sWareinfo + ' ');\r\n	if (typeof (wareinfo) != 'undefined') {\r\n		oConfig.wareinfo = wareinfo;\r\n	}\r\n	return oConfig;\r\n}\r\nfunction getSkuKey(body) {\r\n	var cityId = getCityId(), skuId = '', sSkuKey = '';\r\n	var oSkuidkey_as = body.select('#skuidkey');\r\n	if (oSkuidkey_as && oSkuidkey_as.first()) {\r\n		sSkuKey = oSkuidkey_as.first().ownText();\r\n		return sSkuKey;\r\n	}\r\n	var oEleRegion_as = body.select('div#product-intro > script[type]');\r\n	if (oEleRegion_as && oEleRegion_as.first()) {\r\n		var sEleRegion = oEleRegion_as.first().html();\r\n		eval(' ' + sEleRegion + ' ');\r\n		var oRegionArr = sEleRegion.split('=');\r\n		debug.log('sEleRegion:' + sEleRegion);\r\n		if (oRegionArr.length > 3) {\r\n			var sCitySkuId = oRegionArr[2], sSkuIdKey = oRegionArr[3], sCitySkuId = sCitySkuId\r\n					.substring(0, sCitySkuId.lastIndexOf(';')), sSkuIdKey = sSkuIdKey\r\n					.substring(0, sSkuIdKey.lastIndexOf(';'));\r\n			var oCitySkuIdJSON = eval('(' + sCitySkuId + ')');\r\n			var oSkuIdKeyJSON = eval('(' + sSkuIdKey + ')');\r\n			for ( var n in oCitySkuIdJSON) {\r\n				var citysArr = oCitySkuIdJSON[n].citys;\r\n				for (var i = 0; i < citysArr.length; i++) {\r\n					if (citysArr[i].IdCity == cityId) {\r\n						skuId = citysArr[i].SkuId;\r\n						break;\r\n					}\r\n				}\r\n			}\r\n			if (skuId) {\r\n				for ( var index in oSkuIdKeyJSON) {\r\n					var oSK = oSkuIdKeyJSON[index];\r\n					if (oSK.SkuId == skuId) {\r\n						sSkuKey = oSK.Key;\r\n						break;\r\n					}\r\n				}\r\n			}\r\n			debug.log('090--skuId:' + skuId);\r\n			debug.log('sSkuKey:' + sSkuKey);\r\n		}\r\n	}\r\n	if (sSkuKey) {\r\n		return sSkuKey;\r\n	}\r\n	var head = body.previousElementSibling(), oScript = head\r\n			.select('script[type]');\r\n	for (var i = 0; i < oScript.size(); i++) {\r\n		var sHtml = oScript.get(i).html();\r\n		if (sHtml.contains('pageConfig')) {\r\n			var index = sHtml.indexOf('=');\r\n			sHtml = sHtml.substring(index + 1);\r\n			index = sHtml.indexOf(';');\r\n			if (index > 0) {\r\n				sHtml = sHtml.substring(0, index);\r\n			}\r\n			var oConfigJSON = eval('(' + sHtml + ')');\r\n			debug.log('stock-sSkuKey:' + sHtml);\r\n			if (oConfigJSON.product) {\r\n				sSkuKey = oConfigJSON.product.skuidkey;\r\n			}\r\n			break;\r\n		}\r\n	}\r\n	if (sSkuKey) {\r\n		return sSkuKey;\r\n	}\r\n	var oWareinfo = body.select('#book-price li.sub > script[type]'), sWareinfo = oWareinfo\r\n			.first().html(), oWareJSON = getJSONObj(sWareinfo);\r\n	return oWareJSON.sid;\r\n}\r\nfunction getStockFlagByName(stockStateName) {\r\n	debug.log('stockStateName2:'+stockStateName);\r\n	var sStock = '0';\r\n	if (-1 != stockStateName.indexOf(\"有货\")\r\n			|| -1 != stockStateName.indexOf(\"现货\")\r\n			|| -1 != stockStateName.indexOf(\"在途\")) {\r\n		sStock = \"0\";\r\n	} else if (-1 != stockStateName.indexOf(\"无货\")\r\n			|| -1 != stockStateName.indexOf(\"售完\")) {\r\n		sStock = \"-2\";\r\n	} else if (-1 != stockStateName.indexOf(\"预订\")) {\r\n		sStock = \"0\";\r\n	}\r\n	debug.log('stockStateName2:'+sStock);\r\n	return sStock;\r\n}\r\nfunction getStockFlagByState(state) {\r\n	var sStock = '0';\r\n	if (state == 33) {\r\n		sStock = '0';\r\n	} else if (state == 34 || state == 0) {\r\n		sStock = '-2';\r\n	} else if (state == 39) {\r\n		sStock = '0';\r\n	} else if (state == 40) {\r\n		sStock = '0';\r\n	} else if (state == 36) {\r\n		sStock = '0';\r\n	}\r\n	return sStock;\r\n}\r\nfunction SetNotifyByNoneStock(stockstatus, body) {\r\n	var mvdMark = 'http://mvd.';\r\n	if (args.url.indexOf(mvdMark) > -1) {\r\n		return false;\r\n	}\r\n	var warestatus = '';\r\n	var oEleRegion_as = body.select('div#product-intro > script[type]');\r\n	if (oEleRegion_as && oEleRegion_as.first()) {\r\n		var sEleRegion = oEleRegion_as.first().html();\r\n		debug.log('sEleRegion:' + sEleRegion);\r\n		var wsReg = new RegExp('(warestatus).*?[0-9]+', 'gm'), owsMatch = sEleRegion\r\n				.match(wsReg)\r\n		if (owsMatch && owsMatch[0]) {\r\n			warestatus = owsMatch[0];\r\n			warestatus = warestatus.replace(/[^0-9]+/gm, '');\r\n		}\r\n	}\r\n	debug.log('warestatus:' + warestatus);\r\n	debug.log('stockstatus:' + stockstatus);\r\n	if (stockstatus && stockstatus != 34 && stockstatus != 0 && warestatus == 1) {\r\n		return false;\r\n	}\r\n	return true;\r\n}\r\nfunction getProvinceStockCallback(result, body) {\r\n	var sStock = '0';\r\n	if (result.stock) {\r\n		var stockstate = (result.stock.StockState) ? result.stock.StockState\r\n				: result.stock.S;\r\n		var stockStateName = result.stock.StockStateName;\r\n		debug.log('stockStateName:' + stockStateName);\r\n		sStock = (stockstate) ? getStockFlagByState(stockstate)\r\n				: getStockFlagByName(stockStateName);\r\n		var isNStock = SetNotifyByNoneStock(stockstate, body);\r\n		debug.log(\"isNStock:\" + isNStock);\r\n		sStock = (isNStock) ? '-2' : sStock;\r\n	}\r\n	return sStock;\r\n}\r\nfunction getImgPrice2Num(pid) {\r\n	var sPriceUrl = 'http://p.3.cn/prices/get?skuid=J_' + pid + '&type=1';\r\n	debug.log('sPriceUrl:' + sPriceUrl);\r\n	var sHtml = http.get(sPriceUrl, args);\r\n	debug.log('sHtml:' + sHtml);\r\n	var oPriceArr = eval('(' + sHtml + ')');\r\n	var r = oPriceArr;\r\n	var price = '-1';\r\n	if (!!r && r.length > 0 && r[0].p && r[0].p > 0) {\r\n		price = r[0].p;\r\n	}\r\n	return price;\r\n}\r\nfunction isEmptyVar(param) {\r\n	return 'undefined'.equals(param) || '' == param;\r\n}\r\nfunction getStock(src) {\r\n	var body = src;\r\n\r\n	// var skuId = getSkuKey(body);\r\n	var aimURL = \"\";\r\n	var oRegion = getRegionCodeObj();\r\n	var oConfig = {};\r\n	oConfig = getRegionConfig(body, oConfig);\r\n	oConfig = getPageConfig(body, oConfig);\r\n	var skuId = '';\r\n	var skuKey = '';\r\n\r\n	if (oConfig.pageConfig) {\r\n		var oSkuArr = chooseSkuToArea(oRegion.provinceid, oRegion.cityid,\r\n				oRegion.areaid, oConfig);\r\n		skuKey = oSkuArr[0];\r\n		skuId = oSkuArr[1];\r\n		debug.log(skuKey + \":\" + skuId);\r\n	} else {\r\n		getWareinfo(body, oConfig);\r\n		skuKey = oConfig.wareinfo.sid;\r\n		skuId = oConfig.wareinfo.pid;\r\n	}\r\n	debug.log(\"skuKey:\" + skuKey + \",provinceid:\" + oRegion.provinceid\r\n			+ \",warestatus:\" + oConfig.warestatus);\r\n	if (!isEmptyVar(skuKey) && oRegion.provinceid != 84\r\n			&& oConfig.warestatus == 1) {\r\n	} else if (!isEmptyVar(oConfig.warestatus)) {\r\n		return '-1';\r\n	}\r\n	var stockServiceDomain = 'http://st.3.cn';\r\n	var pageConfig = oConfig.pageConfig;\r\n	var sLocation = '&provinceid=' + oRegion.provinceid + '&cityid='\r\n			+ oRegion.cityid + '&areaid=' + oRegion.areaid + \"&townid=\"\r\n			+ oRegion.townid;\r\n	var aimURL = \"\";\r\n	if (pageConfig && pageConfig.product && pageConfig.product.cat.length >= 3) {\r\n		aimURL = stockServiceDomain + \"/gds.html?skuid=\" + skuKey + sLocation\r\n				+ \"&sortid1=\" + pageConfig.product.cat[0] + \"&sortid2=\"\r\n				+ pageConfig.product.cat[1] + \"&sortid3=\"\r\n				+ pageConfig.product.cat[2] + \"&cd=1_1_1\"\r\n	} else {\r\n		aimURL = \"http://st.3.cn/gds.html?skuid=\" + skuKey + sLocation + \"&t=\"\r\n				+ (new Date()).getTime();\r\n	}\r\n	debug.log('aimURL:' + aimURL);\r\n	var responseStock;\r\n	try {\r\n		responseStock = http.get(aimURL);\r\n	} catch (er) {\r\n		throw \"getstock:\" + aimURL + er;\r\n	}\r\n\r\n	debug.log(\"Multi:\" + responseStock);\r\n	var provinceStockJson = eval(\"(\" + responseStock + \")\");\r\n	var sStock = getProvinceStockCallback(provinceStockJson, body);\r\n	var sPrice = getImgPrice2Num(skuId);\r\n	debug.log('Vef:sStock:' + sStock + \",sPrice:\" + sPrice);\r\n	sStock = (sPrice < 0) ? -1 : sStock;\r\n	return sStock;\r\n}\r\n\r\ntry {\r\n	var body = src;\r\n	var stockStatus = getStock(body);\r\n} catch (ex) {\r\n	throw 'From stock_status,Exception:' + ex;\r\n}";
		try {
			String argsString = "args=" + JSONUtils.getJSONObject(task.getArgs());
			Context cx = Context.enter();
			ScriptableObject scope = cx.initStandardObjects();
			cx.evaluateString(scope, argsString, "<args>", 0, null);
			ScriptableObject.putProperty(scope, "src", dom.select("body").first());
			ScriptableObject.putProperty(scope, "http", new HttpDirector(client));
			cx.evaluateString(scope, source, "<cmd>", 0, null);
			Object stockObject = ScriptableObject.getProperty(scope, "stockStatus");
			Integer stockNum = Integer.valueOf(Context.toString(stockObject));
			int index = stockNum + 2;
			tBean.setStockNum(stockArr[index]);
		} finally {
			Context.exit();
		}
	}

	private void addComment(TargetBean tBean, Document dom) throws Exception {
		// http://club.jd.com/ProductPageService.aspx?method=GetCommentSummaryBySkuId&referenceId=1095329&callback=getCommentCount
		String mUrl = String
				.format("http://club.jd.com/ProductPageService.aspx?method=GetCommentSummaryBySkuId&referenceId=%s&callback=getCommentCount",
						tBean.getProductCode());
		HttpGet get = new HttpGet(mUrl);
		get.addHeader("Referer", dom.baseUri());
		String html = HttpClientUtils.getContent(client, get);
		html = html.replace("getCommentCount", "var oCmm = getCommentCount");
		html = "function getCommentCount(data){return data};" + html;
		html += "var iCmm = oCmm.CommentCount;";
		evaluateString(html, new ScopeCallBack() {
			@Override
			public void doCallBack(ScriptableObject scope, Object targetObject) {
				TargetBean tBean = (TargetBean) targetObject;
				Object cmmObject = ScriptableObject.getProperty(scope, "iCmm");
				tBean.setCommentNum(cmmObject == null ? null : Integer.valueOf(cmmObject.toString()));
			}
		}, tBean);
	}

	private void addAttributes(TargetBean tBean, Document dom) {
		Elements saleTimeAs = dom.select("div[id^=product-detail].mc ul.detail-list li:containsOwn(上架时间)");
		if (!saleTimeAs.isEmpty()) {
			String sSaleTime = saleTimeAs.first().ownText();
			sSaleTime = sSaleTime.replace("上架时间：", "");
			sSaleTime = sSaleTime.replace("上架时间:", "");
			try {
				Date onsailTime = DATE_FORMAT.parse(sSaleTime);
				tBean.setOnsailTime(onsailTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Elements brandAs = dom.select("div[id^=product-detail].mc ul.detail-list li:containsOwn(品牌)");
		if (!brandAs.isEmpty()) {
			String sBrandName = brandAs.first().text();
			sBrandName = sBrandName.replace("品牌：", "");
			sBrandName = sBrandName.replace("品牌:", "");
			tBean.setProductBrand(sBrandName);
		}
		Elements navAs = dom.select("div.breadcrumb");
		if (!brandAs.isEmpty()) {
			String sNav = navAs.first().text();
			sNav = sNav.replaceAll(" ", "");
			String[] navArr = sNav.split(">");
			StringBuilder sb = new StringBuilder();
			int size = navArr.length;
			for (int i = 0; i < 3 && i < size; i++) {
				if (sb.length() < 1) {
					sb.append(navArr[i].trim());
				} else {
					sb.append(";");
					sb.append(navArr[i].trim());
				}
			}
			tBean.setCategoryNav(sb.toString());
		}
		Elements modelAs = dom.select("div[id^=product-detail].mc table.Ptable tr td:matchesOwn(^(型号)|(产品型号)$) + td");
		if (!modelAs.isEmpty()) {
			tBean.setProductModel(modelAs.first().ownText().trim());
		}
		Elements imgAs = dom.select("#spec-list div.spec-items ul.lh li img[src]");
		if (!imgAs.isEmpty()) {
			tBean.setImgUrl(imgAs.first().absUrl("src"));
		}
	}

	private void addPrice(TargetBean tBean, Document dom) throws Exception {
		// http://p.3.cn/prices/get?skuid=J_1095329&type=1&area=1_72_2799&callback=cnp
		// http://p.3.cn/prices/mgets?type=1&skuIds=J_1095329&callback=jsonp1413082152934&_=1413082153605
		if (StringUtils.isEmpty(tBean.getProductCode())) {
			return;
		}
		String sUrl = String.format("http://p.3.cn/prices/mgets?type=1&skuIds=J_%s&callback=jsonp%s&_=%s",
				tBean.getProductCode(), System.currentTimeMillis(), System.currentTimeMillis());
		HttpGet get = new HttpGet(sUrl);
		String html = HttpClientUtils.getContent(client, get);
		html = html.replaceAll("jsonp[0-9]+", "var oData =callback");
		html = "function callback(dataArray){return dataArray[0];};" + html;
		html += " var price=oData.p;";
		html += " var mkprice=oData.m;";

		evaluateString(html, new ScopeCallBack() {
			@Override
			public void doCallBack(ScriptableObject scope, Object targetObject) {
				TargetBean tBean = (TargetBean) targetObject;
				Object pObject = ScriptableObject.getProperty(scope, "price");
				Object mObject = ScriptableObject.getProperty(scope, "mkprice").toString();

				tBean.setProductPrice(pObject == null ? null : Float.valueOf(pObject.toString()));
				tBean.setMarketPrice(mObject == null ? null : Float.valueOf(mObject.toString()));
			}
		}, tBean);
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

	class TargetBean {
		private Integer shopId = 1001;
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

		public Integer getShopId() {
			return shopId;
		}

		public void setShopId(Integer shopId) {
			this.shopId = shopId;
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