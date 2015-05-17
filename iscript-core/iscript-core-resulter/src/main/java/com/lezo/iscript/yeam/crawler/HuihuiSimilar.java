package com.lezo.iscript.yeam.crawler;

import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.script.CommonContext;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpDriver;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class HuihuiSimilar implements ConfigParser {
	private static Logger log = Logger.getLogger(HuihuiSimilar.class);
	private DefaultHttpClient client = HttpDriver.getInstance().getClient();
	private Scriptable scope;

	public HuihuiSimilar() {
		try {
			ScriptableObject parent = CommonContext.getCommonScriptable();
			scope = Context.enter().initStandardObjects(parent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Context.exit();
		}
	}

	public String getName() {
		return "huihui-similar";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject rs = new JSONObject();
		JSONUtils.put(rs, "args", JSONUtils.getJSONObject(task.getArgs()));
		String url = get(task.getArgs(), "url");
		String name = get(task.getArgs(), "name");
		log.info("search:" + name);
		String sUrl = getSimilarUrl(url, name);
		HttpGet get = new HttpGet(sUrl);
		get.addHeader("refer", url);
		String html = HttpClientUtils.getContent(client, get);
		Context cx = Context.enter();
		String source = "var json;" + html.replace("var json", "json");
		source += "json = JSON.stringify(json)";
		cx.evaluateString(scope, source, getName(), 0, null);
		Object jsObject = ScriptableObject.getProperty(scope, "json");
		JSONUtils.put(rs, "rs", Context.toString(jsObject));
		Context.exit();
		return rs.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Map<String, Object> map, String key) {
		if (key == null) {
			return null;
		}
		Object valueObject = map.get(key);
		if (valueObject == null) {
			return null;
		}
		return (T) valueObject;
	}

	public static Integer getInteger(Map<String, Object> map, String key) {
		return get(map, key);
	}

	private String getSimilarUrl(String productUrl, String productName) {
		return getJsonUrl(productUrl, productName);
	}

	private String getJsonUrl(String productUrl, String productName) {
		String urlHead = "http://zhushou.huihui.cn/productSense?av=2.5";
		String m = "m=" + getM(productUrl);
		String k = "k=" + getK(productName);
		return urlHead + '&' + m + '&' + k;
	}

	private StringBuilder getM(String url) {
		Character charUrl;
		StringBuilder encodeParam = new StringBuilder();
		for (int i = 0; i < url.length(); i++) {
			charUrl = url.charAt(i);
			encodeParam.append(enCode(charUrl));
		}
		return encodeParam.reverse();
	}

	private String getK(String title) {
		String param = "t=" + title + "^&k=lxsx^&d=ls";
		Character chrYhdId;
		StringBuilder encodeParam = new StringBuilder();
		for (int i = 0; i < param.length(); i++) {
			chrYhdId = param.charAt(i);
			if (enCode(chrYhdId).length() == 2) {
				encodeParam.append("00" + enCode(chrYhdId));
			} else {
				encodeParam.append(enCode(chrYhdId));
			}
		}
		return encodeParam.toString();
	}

	public String enCode(char a) {
		return Integer.toHexString((int) a + 88);
	}
}
