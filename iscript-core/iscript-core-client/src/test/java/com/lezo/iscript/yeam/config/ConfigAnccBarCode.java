package com.lezo.iscript.yeam.config;

import java.awt.image.BufferedImage;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.crawler.main.AnccScriptCrawler;
import com.lezo.iscript.ocr.ANCCOCRParser;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.rest.http.ImageResponseHandler;
import com.lezo.iscript.rest.http.SimpleHttpBrowser;
import com.lezo.iscript.rest.http.SimpleResponseHandler;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigAnccBarCode implements ConfigParser {
	private static DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static Logger log = LoggerFactory.getLogger(AnccScriptCrawler.class);
	private static final List<String> paramList = new ArrayList<String>();
	static {
		paramList.add("__EVENTARGUMENT");
		paramList.add("__EVENTTARGET");
		paramList.add("__EVENTVALIDATION");
		paramList.add("__VIEWSTATE");
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		DataBean dataBean = getDataObject(task);
		return convert2TaskCallBack(dataBean, task);
	}

	private String convert2TaskCallBack(DataBean dataBean, TaskWritable task) throws Exception {
		JSONObject returnObject = new JSONObject();
		JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, JSONUtils.EMPTY_JSONOBJECT);
		if (dataBean != null) {
			// dataBean.getTargetList().add("ProductDto");
			// dataBean.getTargetList().add("ProductStatDto");

			ObjectMapper mapper = new ObjectMapper();
			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, dataBean);
			String dataString = writer.toString();

			JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
			// JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT,
			// dataString);
		}
		return returnObject.toString();
	}

	private DataBean getDataObject(TaskWritable task) throws Exception {
		String key = task.get("searchKey").toString();
		return searchKey(key);
	}

	private static DataBean searchKey(String key) throws Exception {
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
		String content = getContent(post, "start key:" + key);
		Document dom = Jsoup.parse(content, searchUrl);
		DataBean dataBean = new DataBean();
		while (true) {
			JSONObject pageObj = new JSONObject();
			JSONObject numObj = getNumObj(dom);
			pageObj.put("numObj", numObj);
			JSONArray pArray = getItems(dom);
			pageObj.put("items", pArray);
			String pageString = pageObj.toString();
			log.info(pageString);
			dataBean.getDataList().add(pageString);
			Integer totalPage = Integer.valueOf(numObj.getString("total_page"));
			Integer curPage = Integer.valueOf(numObj.getString("cur_page"));
			if (curPage < totalPage) {
				post = new HttpPost(searchUrl);
				post.setEntity(new UrlEncodedFormEntity(getPageNVList(key, curPage, dom), "gb2312"));
				String html = getContent(post, "nextpage.key:" + key + ",curPage:" + (curPage + 1) + "/"
						+ totalPage);
				if (html == null) {
					log.warn("page.stop at " + curPage + "/" + totalPage);
					break;
				}
				dom = Jsoup.parse(html, searchUrl);
				post = new HttpPost(searchUrl);
				post.setEntity(new UrlEncodedFormEntity(getContentNVList(key, dom), "gb2312"));
				content = getContent(post, "content.key:" + key + ",curPage:" + (curPage + 1) + "/"
						+ totalPage);
				if (content == null) {
					log.warn("content.stop at " + curPage + "/" + totalPage);
					break;
				}
				dom = Jsoup.parse(content, searchUrl);
			} else {
				break;
			}
		}
		return dataBean;
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

	private static String getContent(HttpUriRequest post, String msg) {
		String content = null;
		int index = 0;
		while (index < 3) {
			try {
				// Random random = new Random();
				// long timeout = random.nextInt(2000);
				// timeout += 100;
				// log.info(msg + ",sleep:" + timeout);
				// TimeUnit.MILLISECONDS.sleep(timeout);
				// content = client.execute(post, new SimpleResponseHandler());
				content = HttpClientUtils.getContent(client, post);
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