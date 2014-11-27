package com.lezo.iscript.yeam.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.scope.ScriptableUtils;
import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigBaiduDoc implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static final String EMTPY_RESULT = new JSONObject().toString();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject itemObject = getDataObject(task);
		// doCollect(itemObject, task);
		return itemObject.toString();
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

	/**
	 * {"data":[],"nexts":[]}
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private JSONObject getDataObject(TaskWritable task) throws Exception {
		String url = (String) task.get("url");
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get, "gbk");
		Document dom = Jsoup.parse(html, url);
		Elements scriptAs = dom.select("div#hd > script");
		JSONObject dObject = new JSONObject();
		if (!scriptAs.isEmpty()) {
			String script = scriptAs.first().html();
			String source = "var Func;F ={};F.use=function(jsArray,func){ if(!Func){Func=func;}}; ";
			source += "var Data={};Data.get=function(key){ var value = this[key];return value||{};}; Data.set=function(key,value){this[key]=value};";
			source += script;
			source += "Func(Data);";
			source += "var WkInfo=Data.get('WkInfo');var sParam = WkInfo.htmlUrls;";
			Context cx = Context.enter();
			Scriptable scope = ScriptableUtils.getJSONScriptable();
			cx.evaluateString(scope, source, "<cmd>", 0, null);
			String sParam = Context.toString(ScriptableObject.getProperty(scope, "sParam"));
			JSONObject paramObject = JSONUtils.getJSONObject(sParam);
			JSONArray paramArray = JSONUtils.get(paramObject, "json");
			JSONArray dArray = new JSONArray();
			int len = paramArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject pageObject = paramArray.getJSONObject(i);
				if (pageObject == null) {
					continue;
				}
				String pageLoadUrl = JSONUtils.getString(pageObject, "pageLoadUrl");
				if (!StringUtils.isEmpty(pageLoadUrl)) {
					System.err.println("URL:" + pageLoadUrl);
					HttpGet pageGet = new HttpGet(pageLoadUrl);
					pageGet.addHeader("Accept-Encoding", "gzip, deflate");
					pageGet.addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
					pageGet.addHeader("Referer", url);
					String pHtml = HttpClientUtils.getContent(client, pageGet, "UTF-8");
					Pattern oReg = Pattern.compile("[0-9]{13}");
					Matcher matcher = oReg.matcher(pHtml);
					while (matcher.find()) {
						String sBarCode = matcher.group();
						if (BarCodeUtils.isBarCode(sBarCode)) {
							System.out.println(sBarCode);
							dArray.put(sBarCode);
						}
					}
				}
			}
			JSONUtils.put(dObject, "data", dArray);
		}
		return dObject;
	}
}