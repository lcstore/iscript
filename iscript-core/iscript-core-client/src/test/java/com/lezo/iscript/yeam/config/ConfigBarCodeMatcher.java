package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.DocumentHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigBarCodeMatcher implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static final String EMTPY_RESULT = new JSONObject().toString();
	private List<BarCodeMatcher> matchers;

	public ConfigBarCodeMatcher() {
		this.matchers = new ArrayList<BarCodeMatcher>();
		this.matchers.add(new JdMatcher());
		this.matchers.add(new AmazonMatcher());
		this.matchers.add(new YhdMatcher());
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject itemObject = getDataObject(task);
		doCollect(itemObject, task);
		return itemObject.toString();
	}

	private void doCollect(JSONObject dataObject, TaskWritable task) {
		JSONObject gObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);

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
		ResultBean rsBean = new ResultBean();
		for (BarCodeMatcher m : matchers) {
			Object tBean = m.doMatch(task);
			if (tBean != null) {
				if (tBean instanceof Collection) {
					rsBean.getDataList().addAll((Collection<?>) tBean);
				} else {
					rsBean.getDataList().add(tBean);
				}
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, rsBean);
		return new JSONObject(writer.toString());
	}

	private interface BarCodeMatcher {
		Object doMatch(TaskWritable task);
	}

	private class JdMatcher implements BarCodeMatcher {

		@Override
		public Object doMatch(TaskWritable task) {
			String userAgent = "Dalvik/1.6.0 (Linux; U; Android 4.1.1; MI 2 MIUI/JLB34.0)";
			String oldAgent = HttpProtocolParams.getUserAgent(client.getParams());
			HttpProtocolParams.setUserAgent(client.getParams(), userAgent);
			String barCode = (String) task.getArgs().get("barCode");
			if (!BarCodeUtils.isBarCode(barCode)) {
				return null;
			}
			String sUrl = getSimilarUrl(barCode);
			HttpGet get = new HttpGet(sUrl);
			get.addHeader("refer", "http://gw.m.360buy.com");
			try {
				String html = HttpClientUtils.getContent(client, get);
				System.err.println(html);
				JSONObject dObject = JSONUtils.getJSONObject(html);
				JSONArray wareArray = JSONUtils.get(dObject, "wareInfoList");
				if (wareArray != null && wareArray.length() > 0) {
					dObject = wareArray.getJSONObject(0);
					BarCodeBean tBean = new BarCodeBean();
					tBean.setSiteId(1001);
					tBean.setBarCode(barCode);
					tBean.setProductName(JSONUtils.getString(dObject, "wname"));
					tBean.setProductCode(JSONUtils.getString(dObject, "wareId"));
					tBean.setProductUrl(String.format("http://item.jd.com/%s.html", tBean.getProductCode()));
					return tBean;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				HttpProtocolParams.setUserAgent(client.getParams(), oldAgent);
			}
			return null;
		}

		private String getSimilarUrl(String barCode) {
			String url = "http://gw.m.360buy.com/client.action?functionId=wareIdByBarCodeList&uuid=" + getRandomUid() + "-acf7f34353f1&clientVersion=3.6.3&client=android&d_brand=Xiaomi&d_model=MI2&osVersion=4.1.1&screen=1280*720&partner=jingdong&networkType=wifi&area=2_2841_0_0&sv=1&st=" + System.currentTimeMillis();
			String paramString = "{\"barcode\":\"" + barCode + "\"}";
			try {
				url += "&body=" + URLEncoder.encode(paramString, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return url;
		}

		public String getRandomUid() {
			Integer randomDataLong = new Random().nextInt(10000);
			String head = "860308028232581";
			String ranString = head + randomDataLong.toString();
			return ranString.substring(ranString.length() - head.length());
		}

	}

	private class AmazonMatcher implements BarCodeMatcher {

		@Override
		public Object doMatch(TaskWritable task) {
			String barCode = (String) task.getArgs().get("barCode");
			if (!BarCodeUtils.isBarCode(barCode)) {
				return null;
			}
			String sUrl = getSearchUrl(barCode);
			HttpGet get = new HttpGet(sUrl);
			get.addHeader("refer", "http://www.amazon.cn/?tag=bjlkt7094-23&ascsubtag=A100189775");
			try {
				String html = HttpClientUtils.getContent(client, get);
				Document dom = Jsoup.parse(html);
				Elements rsList = dom.select("#atfResults [id^=result_][name]");
				if (!rsList.isEmpty()) {
					List<BarCodeBean> beanList = new ArrayList<ConfigBarCodeMatcher.BarCodeBean>(rsList.size());
					for (Element rsEle : rsList) {
						BarCodeBean tBean = new BarCodeBean();
						tBean.setSiteId(1003);
						tBean.setBarCode(barCode);
						tBean.setProductCode(rsEle.attr("name").trim());
						tBean.setProductUrl(String.format("http://www.amazon.cn/dp/%s", tBean.getProductCode()));
						Elements nameList = rsEle.select("h3.newaps");
						if (!nameList.isEmpty()) {
							tBean.setProductName(nameList.get(0).text());
						}
						beanList.add(tBean);
					}
					return beanList;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		private String getSearchUrl(String keywords) {
			return String.format("http://www.amazon.cn/s/ref=nb_sb_noss?__mk_zh_CN=亚马逊网站&url=search-alias=aps&field-keywords=%s", keywords);
		}

	}

	private class YhdMatcher implements BarCodeMatcher {
		private ObjectMapper objectMapper = new ObjectMapper();

		// private XmlMapper xmlMapper = new XmlMapper();

		@Override
		public Object doMatch(TaskWritable task) {
			String barCode = (String) task.getArgs().get("barCode");
			if (!BarCodeUtils.isBarCode(barCode)) {
				return null;
			}
			String sUrl = "http://interface.m.yhd.com/centralmobile/servlet/CentralMobileFacadeServlet";
			try {
				HttpPost post = new HttpPost(sUrl);
				addHeaders(post);
				addPostParams(post, barCode);
				String html = HttpClientUtils.getContent(client, post);
				if (html.lastIndexOf("barcodeKeyword") < 0) {
					org.dom4j.Document document = DocumentHelper.parseText(html);
					org.dom4j.Element root = document.getRootElement();
					List<org.dom4j.Element> eleList = root.elements();
					List<BarCodeBean> beanList = new ArrayList<ConfigBarCodeMatcher.BarCodeBean>(eleList.size());
					for (org.dom4j.Element pVoEle : eleList) {
						BarCodeBean tBean = new BarCodeBean();
						tBean.setSiteId(1002);
						tBean.setBarCode(barCode);
						tBean.setProductCode(pVoEle.elementTextTrim("pmId"));
						tBean.setProductUrl(String.format("http://item.yhd.com/item/%s", tBean.getProductCode()));
						tBean.setProductName(pVoEle.elementTextTrim("cnName"));
						beanList.add(tBean);

					}
					return beanList;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		private void addHeaders(HttpPost post) {
			post.addHeader("userToken", "");
			post.addHeader("provinceId", "1");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			post.addHeader("clientInfo", "{\"clientAppVersion\":\"3.1.7\",\"clientSystem\":\"android\",\"clientVersion\":\"MI 2,16,4.1.1\",\"deviceCode\":\"ffffffff-8070-baad-ffff-ffff8022f4cb\",\"interfaceVersion\":\"1.3.5\",\"nettype\":\"wifi\",\"traderName\":\"androidSystem\",\"unionKey\":\"8149237\"}");
		}

		private void addPostParams(HttpPost post, String barCode) throws Exception {
			List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
			paramPairs.add(new BasicNameValuePair("methodName", "getProductByBarcode"));
			paramPairs.add(new BasicNameValuePair("methodBody", "<object-array><com.yihaodian.mobile.vo.bussiness.Trader><clientAppVersion>3.1.7</clientAppVersion><clientSystem>android</clientSystem><clientVersion>MI 2,16,4.1.1</clientVersion><deviceCode>ffffffff-8070-baad-ffff-ffff8022f4cb</deviceCode><interfaceVersion>1.3.5</interfaceVersion><protocol>HTTPXML</protocol><traderName>androidSystem</traderName><traderPassword>sCarce!8</traderPassword><unionKey>8149237</unionKey></com.yihaodian.mobile.vo.bussiness.Trader><string>" + barCode + "</string><long>1</long></object-array>"));
			post.setEntity(new UrlEncodedFormEntity(paramPairs, "UTF-8"));
		}
	}

	private class BarCodeBean {
		private Integer siteId;
		private String barCode;
		private String productCode;
		private String productName;
		private String productUrl;

		public Integer getSiteId() {
			return siteId;
		}

		public void setSiteId(Integer siteId) {
			this.siteId = siteId;
		}

		public String getBarCode() {
			return barCode;
		}

		public void setBarCode(String barCode) {
			this.barCode = barCode;
		}

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

	}

	private class ResultBean {
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