package com.lezo.iscript.yeam.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.crawler.script.CommonContext;
import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.client.event.CallEvent;
import com.lezo.iscript.yeam.client.event.EventManager;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Scope;

public class Detail1688 implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(Detail1688.class);
	private DefaultHttpClient client = HttpClientUtils.createHttpClient();

	@Override
	public String getName() {
		return "1688-detail";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		Map<String, Object> argsMap = task.getArgs();
		if (argsMap.containsKey("isList")) {
			return addDetailUrls(task);
		}
		JSONObject rsObject = getResultObject(task, client);
		return rsObject.toString();
	}

	private String addDetailUrls(TaskWritable task) throws Exception {
		String listUrl = (String) task.get("url");
		EventManager eventManager = EventManager.getInstance();
		JSONObject rsObject = new JSONObject();
		JSONUtils.put(rsObject, "args", new JSONObject(task.getArgs()));
		int total = 0;
		int pageCount = 0;
		while (true) {
			pageCount++;
			HttpGet get = new HttpGet(listUrl);
			String html = HttpClientUtils.getContent(client, get);
			Document dom = Jsoup.parse(html, listUrl);
			total += parseItems(dom,task,listUrl);
			Elements dataEles=dom.select("#sw_mod_pagination_content[data-mod-config],#sw_delayload_url[data-mod-config]");
			for(Element dEle:dataEles){
				String dataString = dEle.attr("data-mod-config");
				JSONObject dObject =JSONUtils.getJSONObject(dataString);
				String loadUrl =JSONUtils.getString(dObject, "url");
				if(StringUtils.isEmpty(loadUrl)){
					continue;
				}
				HttpGet loadGet = new HttpGet(loadUrl);
				html = HttpClientUtils.getContent(client, loadGet);
				int index = html.indexOf("(");
				html = "var loadHtml = callBack"+html.substring(index);
				Context cx = Context.enter();
				ScriptableObject parent =CommonContext.getCommonScriptable();
				Scriptable scope =cx.initStandardObjects(parent);
				String source = "function callBack(data){return data.content.offerResult.html;}; "+html;
				cx.evaluateString(scope, source, "cmd", 0, null);
				Object loadObject = ScriptableObject.getProperty(scope, "loadHtml");
				html = Context.toString(loadObject);
				Document jDom = Jsoup.parse(html, listUrl);
				total += parseItems(jDom,task,listUrl);
			}
			Elements nextAs = dom.select("div.page-bottom a.page-next[href]");
			if (!nextAs.isEmpty()) {
				listUrl = nextAs.first().absUrl("href");
			} else {
				break;
			}
		}
		JSONUtils.put(rsObject, "total", total);
		JSONUtils.put(rsObject, "pageCount", pageCount);
		return rsObject.toString();
	}

	private int parseItems(Document dom, TaskWritable task, Object listUrl) throws Exception {
		Elements elements = dom
				.select("li[id^=offer] h2.sm-offerShopwindow-title a.sm-offerShopwindow-titleLink[href]");
		for (Element e : elements) {
			String url = e.absUrl("href");
			if (StringUtils.isEmpty(url)) {
				continue;
			}
			TaskWritable taskWritable = new TaskWritable();
			taskWritable.setId(task.getId());
			for (Entry<String, Object> entry : task.getArgs().entrySet()) {
				if ("isList".equals(entry.getKey()) || "url".equals(entry.getKey())) {
					continue;
				}
				taskWritable.put(entry.getKey(), entry.getValue());
			}
			taskWritable.put("fromUrl", listUrl);
			taskWritable.put("url", url);
//			eventManager.notifyEvent(new CallEvent(taskWritable, CallEvent.FETCH_TASK_EVENT));
			JSONObject iObject = getResultObject(taskWritable, client);
			logger.info(iObject.toString());
		}
		return elements.size();
	}

	private JSONObject getResultObject(TaskWritable task, DefaultHttpClient client) throws Exception {
		String url = task.get("url").toString();
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get, "gbk");
		Document dom = Jsoup.parse(html, url);
		Elements elements = dom.select("div#mod-detail-hd.mod-detail-hd h1.d-title");
		JSONObject itemObject = new JSONObject();
		if(!elements.isEmpty()){
			JSONUtils.put(itemObject, "name", elements.first().ownText());
		}else {
			logger.warn("can not get name:"+url);
		}

		Elements attrElements = dom.select("div#mod-detail-attributes table tr td.de-feature");
		for (Element ele : attrElements) {
			String attr = ele.ownText();
			int index = attr.indexOf("：");
			index = index < 0 ? attr.indexOf(":") : index;
			String key = index < 0 ? attr : attr.substring(0, index);
			String value = index < 0 ? attr : attr.substring(index + 1);
			JSONUtils.put(itemObject, key, value);
		}
		Elements priceElements = dom.select("table.de-price-hd tr[data-range]");
		if(!priceElements.isEmpty()){
			JSONUtils.put(itemObject, "dataRange", priceElements.first().attr("data-range"));
		}
		Pattern oReg = Pattern.compile("[0-9]{13,}");
		Matcher matcher = oReg.matcher(html);
		if (matcher.find()) {
			JSONUtils.put(itemObject, "barCode", matcher.group());
		}
		JSONObject rsObject = new JSONObject();
		rsObject.put("rs", itemObject);
		rsObject.put("args", new JSONObject(task.getArgs()));
		return rsObject;
	}

}
