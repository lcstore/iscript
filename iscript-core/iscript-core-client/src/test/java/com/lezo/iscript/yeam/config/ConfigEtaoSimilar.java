package com.lezo.iscript.yeam.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.URLUtils;
import com.lezo.iscript.yeam.client.HardConstant;
import com.lezo.iscript.yeam.http.HttpRequestManager;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.simple.utils.ClientPropertiesUtils;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigEtaoSimilar implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigEtaoSimilar.class);
	private HttpRequestManager httpRequestManager;
	private List<String> detectUrls;
	private static final String DETECTOR = String.format("%s@%s", ClientPropertiesUtils.getProperty("name"),
			HardConstant.MAC_ADDR);

	public ConfigEtaoSimilar() {
		httpRequestManager = new HttpRequestManager();
		httpRequestManager.getClient().setRoutePlanner(null);
		detectUrls = new ArrayList<String>();
		detectUrls.add("http://www.baidu.com/index.php?tn=19045005_6_pg");
		detectUrls.add("http://detail.tmall.com/item.htm?id=17031847966");
		detectUrls.add("http://item.jd.com/856850.html");
		detectUrls.add("http://detail.1688.com/offer/36970162715.html");
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		Integer port = (Integer) task.get("port");
		String url = null;
		JSONObject itemObject = new JSONObject();
		// HttpGet get = new HttpGet(url);
		long start = System.currentTimeMillis();
		String productName = "Hahne亨利 玉米片 375g 德国进口";
		Long productPmId = 101199L;
		String mUrl = getEtaoMatchUrl(productPmId, productName);
		System.out.println(mUrl);
		long cost = System.currentTimeMillis() - start;
		JSONUtils.put(itemObject, "url", url);
		JSONUtils.put(itemObject, "cost", cost);
		return itemObject.toString();
	}

	private String getEtaoMatchUrl(Long productPmId, String productName) {
		String productUrl = "http://www.yihaodian.com/item/" + productPmId;
		String urlHead = "http://ruyi.etao.com/ext/productSearch?q=";
		String urlBody = "{\"url\":\"" + productUrl + "?&ali_crowd=\",\"title\":\"" + productName + "\"}";
		try {
			urlBody = URLEncoder.encode(new String(urlBody.getBytes("UTF-8"), "GBK"), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return urlHead + urlBody;
	}
}
