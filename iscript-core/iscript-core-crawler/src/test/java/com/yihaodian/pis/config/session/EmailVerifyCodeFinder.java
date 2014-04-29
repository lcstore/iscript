package com.yihaodian.pis.config.session;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class EmailVerifyCodeFinder {

	public String getVerifyCode(final String emaiilAddr, final String pwd) throws Exception {
		LoginSimulator login = new LoginSimulator();
		int index = emaiilAddr.indexOf("@");
		final String username = (index > -1) ? emaiilAddr.substring(0, index) : emaiilAddr;
		final String password = pwd;
		login.addRequest(new RequestGetable() {
			@Override
			public HttpUriRequest nextRequest(HttpClient client, HttpUriRequest request) throws Exception {
				String uri = "https://ssl.mail.163.com/entry/coremail/fcg/ntesdoor2?df=mail163_letter&from=web&funcid=loginone&iframe=1&language=-1&net=t&passtype=1&product=mail163&race=110_110_141_gz&style=-1&uid="
						+ username + "@163.com";
				HttpPost next = new HttpPost(uri);
				List<NameValuePair> pairList = new ArrayList<NameValuePair>();
				pairList.add(new BasicNameValuePair("username", username));
				pairList.add(new BasicNameValuePair("password", password));
				pairList.add(new BasicNameValuePair("savelogin", "0"));
				pairList.add(new BasicNameValuePair("url2", "http://mail.163.com/errorpage/err_163.htm"));

				HttpEntity entity = new UrlEncodedFormEntity(pairList, "utf-8");
				next.setEntity(entity);

				return next;
			}
		});
		login.addRequest(new RequestGetable() {
			@Override
			public HttpUriRequest nextRequest(HttpClient client, HttpUriRequest request) throws Exception {
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				Document dom = Jsoup.parse(content);
				Elements scriptEls = dom.select("head script[type]");
				String destUrlName = "top.location.href";
				String urlHtml = null;
				for (Element ele : scriptEls) {
					String script = ele.html();
					if (script.indexOf(destUrlName) > -1) {
						urlHtml = script;
						break;
					}
				}
				Context cx = Context.enter();
				Scriptable scope = cx.initStandardObjects();
				String html = "var top = {}; top.location={}; ";
				html += urlHtml;
				html += "var nextUrl = top.location.href;";
				// html = "eval(" + html + ")";
				System.out.println("html:" + html);
				cx.evaluateString(scope, html, "<script>", 1, null);
				String destUrl = (String) scope.get("nextUrl", scope);
				System.out.println("destUrl:" + Context.toString(destUrl));
				HttpGet next = new HttpGet(destUrl);

				return next;
			}
		});
		login.addRequest(new SearchEmalRequestGetter());
		login.addRequest(new FirstEmalRequestGetter());

		DefaultHttpClient client = new DefaultHttpClient();
		HttpUriRequest request = null;
		request = login.doLogin(client, request);

		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		String content = EntityUtils.toString(entity);
		System.out.println("Verify:" + content);
		Pattern vCodeReg = Pattern.compile("输入.*?([0-9]{6}).*?完成操作");
		Matcher matcher = vCodeReg.matcher(content);
		if (matcher.find(1)) {
			return matcher.group(1);
		}
		return null;
	}

	class SearchEmalRequestGetter implements RequestGetable {

		@Override
		public HttpUriRequest nextRequest(HttpClient client, HttpUriRequest request) throws Exception {
			StringBuilder sb = new StringBuilder();
			String searchKey = "淘宝邮箱验证";
			sb.append("<?xml version=\"1.0\"?><object><string name=\"operator\">or</string><array name=\"condictions\"><object><string name=\"field\">");
			sb.append("subject</string><string name=\"operator\">contains</string><boolean name=\"ignoreCase\">true</boolean><string name=\"operand\">");
			sb.append("" + searchKey + "</string></object><object><string name=\"field\">from</string>");
			sb.append("<string name=\"operator\">contains</string><boolean name=\"ignoreCase\">true</boolean>");
			sb.append("<string name=\"operand\">" + searchKey
					+ "</string></object><object><string name=\"field\">to</string>");
			sb.append("<string name=\"operator\">contains</string><boolean name=\"ignoreCase\">true</boolean>");
			sb.append("<string name=\"operand\">" + searchKey + "</string></object></array>");
			sb.append("<array name=\"fid\"><int>1</int><int>2</int><int>3</int><int>18</int><int>5</int></array><string name=\"order\">date</string>");
			sb.append("<boolean name=\"desc\">true</boolean><int name=\"windowSize\">20</int><int name=\"summaryWindowSize\">20</int>");
			sb.append("<boolean name=\"returnTag\">true</boolean></object>");

			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity);
			System.out.println("SearchEmalRequestGetter:" + content);

			Document dom = Jsoup.parse(content);
			Elements scriptEls = dom.select("#spnCheckFont + script[type]");
			String html = null;
			for (Element ele : scriptEls) {
				String script = ele.html();
				if (script.indexOf("gEnvironment") > -1) {
					html = script;
					break;
				}
			}

			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();
			String prefixHtml = "var location ={}; location.hostname='twebmail.mail'; ";
			String suffixHtml = "var readUrl ='http://'+gEnvironment.host+'.' +gEnvironment.domain+'/'+gEnvironment.ver+'/'+gEnvironment.readUrl;";
			String nextUrlHtml = "var searchUrl ='http://'+gEnvironment.host+'.' +gEnvironment.domain+'/'+gEnvironment.ver+'/s?sid='+gUser.sid;";
			html = prefixHtml + html + suffixHtml + nextUrlHtml;
			// html = "eval(" + html + ")";
			System.out.println("html:" + html);
			cx.evaluateString(scope, html, "<script>", 1, null);
			String readUrl = (String) scope.get("readUrl", scope);
			String searchUrl = (String) scope.get("searchUrl", scope);
			System.out.println("readUrl:" + readUrl);
			String suffixNextUrl = "&func=mbox:searchMessages&welc=rcmdtab0&action=global&click=searchArrIcon";
			searchUrl += suffixNextUrl;
			System.out.println("SearchEmalRequestGetter.nextUrl:" + searchUrl);
			System.out.println("SearchEmalRequestGetter.sb:" + sb.toString());
			HttpPost next = new HttpPost(searchUrl);
			List<NameValuePair> pairList = new ArrayList<NameValuePair>();
			pairList.add(new BasicNameValuePair("var", sb.toString()));
			// pairList.add(new BasicNameValuePair("func",
			// "mbox:searchMessages"));
			pairList.add(new BasicNameValuePair("welc", "rcmdtab4|rcmdauto|rcmdtab5|rcmdtab6&action"));
			// pairList.add(new BasicNameValuePair("action","global"));
			// pairList.add(new BasicNameValuePair("click","searchArrIcon"));

			HttpEntity postEntity = new UrlEncodedFormEntity(pairList, "utf-8");
			next.setEntity(postEntity);
			next.addHeader("readUrl", readUrl);
			next.addHeader("Accept", "text/javascript");
			// next.addHeader("Referer", request.getURI().toString());
			return next;
		}
	}

	class FirstEmalRequestGetter implements RequestGetable {

		@Override
		public HttpUriRequest nextRequest(HttpClient client, HttpUriRequest request) throws Exception {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity, "utf-8");
			System.out.println("FirstEmalRequestGetter:" + content);

			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();
			String sVar = "var newEmailId = oEmail['var'][0].id;";
			String html = "var oEmail =" + content + ";" + sVar;
			cx.evaluateString(scope, html, "<script>", 1, null);
			String newEmailId = (String) scope.get("newEmailId", scope);

			String readUrl = request.getFirstHeader("readUrl").getValue();
			String nextUrl = readUrl + "&mid=" + newEmailId + "&color=003399";
			System.out.println("readUrl:" + nextUrl);
			HttpGet next = new HttpGet(nextUrl);
			return next;
		}
	}
}
