package com.lezo.iscript.crawler.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.crawler.http.AnccVerifyRequestInterceptor;
import com.lezo.iscript.crawler.http.HttpBrowserManager;
import com.lezo.iscript.crawler.http.ImageResponseHandler;
import com.lezo.iscript.crawler.http.SimpleHttpBrowser;
import com.lezo.iscript.crawler.http.SimpleResponseHandler;
import com.lezo.iscript.ocr.ANCCOCRParser;

public class AnccScriptCrawler {
	private static Logger log = Logger.getLogger(AnccScriptCrawler.class);
	private static Logger rsLogger = Logger.getLogger(ResultLogger.class);
	private static final List<String> paramList = new ArrayList<String>();
	static {
		paramList.add("__EVENTARGUMENT");
		paramList.add("__EVENTTARGET");
		paramList.add("__EVENTVALIDATION");
		paramList.add("__VIEWSTATE");
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			args = new String[] { "src/main/resources/region.txt" };
		}
		String regionPath = args[0];
		String name = "ancc";
		SimpleHttpBrowser browser = HttpBrowserManager.buildBrowser(name);
		HttpRequestInterceptor itcp = new AnccVerifyRequestInterceptor();
		browser.getHttpClient().addRequestInterceptor(itcp);
		String key = "牛奶";
		List<String> suffixList = new ArrayList<String>();
		suffixList.add("省");
		suffixList.add("市");
		suffixList.add("自治区");
		suffixList.add("特别行政区");
		List<String> regions = FileUtils.readLines(new File(regionPath));
		int i = 0;
		for (String region : regions) {
			key = region;
			for (String suffix : suffixList) {
				int index = region.indexOf(suffix);
				if (index > 0) {
					key = region.substring(0, index);
					break;
				}
			}
			log.info("start to search region:" + region + ", key:" + key + "," + (++i) + "/" + regions.size());
			searchKey(key, browser);
		}
	}

	private static void searchKey(String key, SimpleHttpBrowser browser) throws Exception {
		String searchUrl = "http://search.anccnet.com/searchResult2.aspx?keyword=11111111111111";
		HttpPost post = new HttpPost(searchUrl);
		post.addHeader("Referer", searchUrl);
		post.addHeader("Host", "search.anccnet.com");
		post.addHeader("Accept-Language", "zh-cn");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		List<NameValuePair> nvList = new ArrayList<NameValuePair>();
		nvList.add(new BasicNameValuePair("gdsBtn", "商品搜索"));
		nvList.add(new BasicNameValuePair("keyword", key));
		nvList.add(new BasicNameValuePair("__EVENTVALIDATION",
				"/wEWAwKi+7rnCALd5eLQCQLmjL2EBzZQkOCSCOeVC67jESmqP3f/OGlG"));
		nvList.add(new BasicNameValuePair(
				"__VIEWSTATE",
				"/wEPDwULLTEzODQxNzY5NjMPZBYCAgEPZBYEAgUPDxYCHgRUZXh0BSHmmoLml7bmsqHmnInmgqjopoHmib7nmoTllYblk4HvvIFkZAILDxYCHgdWaXNpYmxlaGRk/dIr6rIvYV5BnmT57ev61/rVCrI="));
		nvList.add(new BasicNameValuePair("__EVENTTARGET", ""));
		nvList.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
		post.setEntity(new UrlEncodedFormEntity(nvList, "gb2312"));
		String content = getContent(browser, post, "start key:" + key);
		Document dom = Jsoup.parse(content, searchUrl);
		while (true) {
			JSONObject pageObj = new JSONObject();
			JSONObject numObj = getNumObj(dom);
			pageObj.put("numObj", numObj);
			JSONArray pArray = getItems(dom);
			pageObj.put("items", pArray);
			rsLogger.info(pageObj.toString());
			Integer totalPage = Integer.valueOf(numObj.getString("total_page"));
			Integer curPage = Integer.valueOf(numObj.getString("cur_page"));
			if (curPage < totalPage) {
				post.setEntity(new UrlEncodedFormEntity(getPageNVList(key, curPage, dom), "gb2312"));
				String html = getContent(browser, post, "nextpage.key:" + key + ",curPage:" + (curPage + 1) + "/"
						+ totalPage);
				if (html == null) {
					log.warn("page.stop at " + curPage + "/" + totalPage);
					return;
				}
				dom = Jsoup.parse(html, searchUrl);
				post.setEntity(new UrlEncodedFormEntity(getContentNVList(key, dom), "gb2312"));
				content = getContent(browser, post, "content.key:" + key + ",curPage:" + (curPage + 1) + "/"
						+ totalPage);
				if (content == null) {
					log.warn("content.stop at " + curPage + "/" + totalPage);
					return;
				}
				dom = Jsoup.parse(content, searchUrl);
			} else {
				break;
			}
		}
	}

	private static List<NameValuePair> getPageNVList(String key, int curPage, Document dom) {
		if (key.equals("香港") && curPage < 540) {
			curPage = 540;
		}
		List<NameValuePair> nvList = new ArrayList<NameValuePair>();
		nvList.add(new BasicNameValuePair("keyword", key));
		nvList.add(new BasicNameValuePair("__EVENTTARGET", "myPager"));
		nvList.add(new BasicNameValuePair("__EVENTARGUMENT", "" + (curPage + 1)));
		for (int i = 2; i < paramList.size(); i++) {
			String param = paramList.get(i);
			Elements oParamAs = dom.select("#" + param + "[value]");
			if (oParamAs.isEmpty()) {
				nvList.add(new BasicNameValuePair(param, ""));
			} else {
				String value = oParamAs.first().attr("value");
				nvList.add(new BasicNameValuePair(param, value));
			}
		}
		return nvList;
	}

	private static List<NameValuePair> getContentNVList(String key, Document dom) {
		List<NameValuePair> nvList = new ArrayList<NameValuePair>();
		nvList.add(new BasicNameValuePair("keyword", key));
		nvList.add(new BasicNameValuePair("gdsBtn", "商品搜索"));

		for (int i = 0; i < paramList.size(); i++) {
			String param = paramList.get(i);
			Elements oParamAs = dom.select("#" + param + "[value]");
			if (oParamAs.isEmpty()) {
				nvList.add(new BasicNameValuePair(param, ""));
			} else {
				String value = oParamAs.first().attr("value");
				nvList.add(new BasicNameValuePair(param, value));
			}
		}
		return nvList;
	}

	private static String getContent(SimpleHttpBrowser browser, HttpUriRequest post, String msg) {
		String content = null;
		int index = 0;
		while (index < 3) {
			try {
				Random random = new Random();
				long timeout = random.nextInt(2000);
				timeout += 100;
				log.info(msg + ",sleep:" + timeout);
				TimeUnit.MILLISECONDS.sleep(timeout);
				content = browser.execute(post, new SimpleResponseHandler());
				break;
			} catch (Exception e) {
				log.warn(msg + ",index:" + (++index), e);
			}
		}
		return content;
	}

	private static JSONObject getNumObj(Document dom) throws Exception {
		JSONObject numObj = new JSONObject();
		Elements oPagerAs = dom.select("#myPager td:contains(总记录数) b:matches(\\d+)");
		if (oPagerAs.size() == 3) {
			int index = -1;
			numObj.put("total", oPagerAs.get(++index).ownText().trim());
			numObj.put("total_page", oPagerAs.get(++index).ownText().trim());
			numObj.put("cur_page", oPagerAs.get(++index).ownText().trim());
		} else {
			numObj.put("total", "0");
			numObj.put("total_page", "0");
			numObj.put("cur_page", "0");
		}
		return numObj;
	}

	private static JSONArray getItems(Document dom) throws Exception {
		JSONArray pArray = new JSONArray();
		Elements rsEls = dom.select("#results li div.result");
		for (Element ele : rsEls) {
			JSONObject pObj = new JSONObject();
			pArray.put(pObj);
			Elements oImgAs = ele.select("img[id$=_productimg][src]");
			if (!oImgAs.isEmpty()) {
				String imgUrl = oImgAs.first().absUrl("src");
				pObj.put("pImg", imgUrl);
			}
			Elements oBrandAs = ele.select("dl.p-supplier dt:contains(商标) + dd");
			if (!oBrandAs.isEmpty()) {
				String value = oBrandAs.first().ownText();
				pObj.put("pBrand", value);
			}
			Elements oSupperAs = ele.select("a[id$=_firmLink][href]");
			if (!oSupperAs.isEmpty()) {
				String value = oSupperAs.first().ownText();
				pObj.put("spName", value);
				value = oSupperAs.first().absUrl("href");
				pObj.put("spUrl", value);
			}
			Elements oProductAs = ele.select("dl.p-info dt:contains(商品条码) + dd a[href]");
			if (!oProductAs.isEmpty()) {
				String value = oProductAs.first().ownText().trim();
				pObj.put("pBarCode", value);
				value = oProductAs.first().absUrl("href");
				pObj.put("pUrl", value);
			}
			Elements oNameAs = ele.select("dl.p-info dt:contains(名称) + dd");
			if (!oNameAs.isEmpty()) {
				String value = oNameAs.first().ownText().trim();
				pObj.put("pName", value);
			}
			Elements oModelAs = ele.select("dl.p-info dt:contains(规格型号) + dd");
			if (!oModelAs.isEmpty()) {
				String value = oModelAs.first().ownText().trim();
				pObj.put("pModel", value);
			}
			Elements oDescribeAs = ele.select("dl.p-info dt:contains(描述) + dd");
			if (!oDescribeAs.isEmpty()) {
				String value = oDescribeAs.first().ownText().trim();
				pObj.put("pText", value);
			}
		}
		return pArray;
	}

	private static String ocrVerifyCode(SimpleHttpBrowser vBrowser) throws Exception {
		String url = "http://search.anccnet.com/comm/select_CheckCodeImg.aspx?id=" + Math.random();
		HttpUriRequest request = new HttpGet(url);
		BufferedImage image = vBrowser.execute(request, new ImageResponseHandler());
		String tessPath = "C:/Tesseract-ocr";
		String result = ANCCOCRParser.doParse(tessPath, image);
		return result;
	}

	private static String getVerifyCode(SimpleHttpBrowser browser) throws Exception {
		String vCode = "";
		int index = 0;
		while (!isValidate(browser, vCode)) {
			Random random = new Random();
			int timeout = random.nextInt(2000);
			timeout += 100;
			System.out.println("sleep " + timeout + "ms.retry(" + (++index) + ")");
			TimeUnit.MILLISECONDS.sleep(timeout);
			vCode = ocrVerifyCode(browser);
		}
		return vCode;
	}

	public static boolean isValidate(SimpleHttpBrowser vBrowser, String vCode) throws Exception {
		if (!ANCCOCRParser.isVerifyCode(vCode)) {
			return false;
		}
		String sUrl = "http://search.anccnet.com/comm/ajax.aspx?search_subjoin=" + vCode;
		String sContent = null;
		HttpUriRequest request = new HttpGet(sUrl);
		try {
			sContent = vBrowser.execute(request, new SimpleResponseHandler());
		} catch (Exception e) {
		}
		if (sContent == null || "" == sContent.trim()) {
			return false;
		}
		JSONObject jObj = new JSONObject(sContent);
		return jObj.has("state") && 1 == jObj.getInt("state");
	}

}
