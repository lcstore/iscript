package com.lezo.iscript.yeam.config;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.envjs.EnvjsUtils;
import com.lezo.iscript.envjs.dom.DocumentAdapt;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ZYueSigner implements ConfigParser {

	@Override
	public String getName() {
		return "zyue-sign";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String username = (String) task.get("user");
		String password = (String) task.get("pwd");
		DefaultHttpClient client = HttpClientUtils.createHttpClient();

		BasicClientCookie cookie = new BasicClientCookie("__utma",
				"193324902.1016719687.1401026096.1401026096.1401026096.1");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmc", "193324902");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("__utmz",
				"193324902.1401026096.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
		client.getCookieStore().addCookie(cookie);
		// addCookie(client, scope);
		String url = "http://ah2.zhangyue.com/zybook3/app/app.php?ca=sign.Index&key=&usr=" + username + "&rgt=7";

		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html, get.getURI().toString());
		Elements signAs = dom.select("#submit_sing[onclick]");
		String result = "fail to sign.";
		if (!signAs.isEmpty()) {
			String signCode = signAs.first().attr("onclick");
			Elements scriptAs = dom.select("body script");
			Elements rewriteAs = dom.select("#winner_rewrite");
			String mark = "gotoUrlWithSeedValidate";
			String markOther = "view_award_names";
			StringBuilder sb = new StringBuilder();
			sb.append("var b = document.createElement('b');");
			sb.append("b.id='winner_rewrite';");
			sb.append("b.style='margin-top:12px';");
			if (rewriteAs.isEmpty()) {
				sb.append("b['data-item']=1;");
			}else {
				sb.append("b['data-item']="+rewriteAs.first().attr("data-item")+";");
			}
			sb.append("b.setIdAttribute('id',true);");
			for (Element e : scriptAs) {
				String curString = e.html();
				if (curString.indexOf(mark) > 0 || curString.indexOf(markOther) > 0) {
//					sb.append(e.html());
				}
			}
//			sb.append("(function() {" + signCode + "})();");
			sb.append("var signUrl = location.href;");

			Scriptable scope = EnvjsUtils.initStandardObjects(null);
			Context cx = EnvjsUtils.enterContext();
//			cx.evaluateString(scope, sb.toString(), "cmd", 0, null);
			DocumentAdapt document = (DocumentAdapt) ScriptableObject.getProperty(scope, "document");
			
			String signUrl = Context.toString(ScriptableObject.getProperty(scope, "signUrl"));
			signUrl = encodeUrl(signUrl);
			get = new HttpGet(signUrl);
			get.addHeader("Referer", url);
			html = HttpClientUtils.getContent(client, get);

			dom = Jsoup.parse(html, signUrl);
			Elements luckyElements = dom.select("div.p10");
			String luckyDraw = luckyElements.isEmpty() ? "No lucky." : luckyElements.text();
			String nextSignUrl = signUrl.replace("sign.turncard", "sign.registration");
			nextSignUrl = encodeUrl(nextSignUrl);
			get = new HttpGet(nextSignUrl);
			get.addHeader("Referer", signUrl);
			result = luckyDraw;
			if (!luckyElements.isEmpty()) {
				String luckyString = HttpClientUtils.getContent(client, get);
				result += ", " + luckyString;
			}
		}

		JSONObject rsObject = new JSONObject();
		rsObject.put("rs", result);
		task.getArgs().remove("pwd");
		rsObject.put("args", new JSONObject(task.getArgs()));

		return rsObject.toString();
	}

	private String encodeUrl(String url) throws Exception {
		url = url.replace("|", "%7C");
		return url;
	}
}
