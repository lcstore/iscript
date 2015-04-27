package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.scope.ScriptableUtils;
import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.http.HttpClientFactory;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.http.ProxySocketFactory;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxyRegion implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigProxyRegion.class);
	private DefaultHttpClient client;

	public ConfigProxyRegion() {
		this.client = HttpClientFactory.createHttpClient();
		this.client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(1, false));
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
		dataBean.getTargetList().add("ProxyRegionVo");

		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, dataBean);
		String dataString = writer.toString();

		JSONObject returnObject = new JSONObject();
		JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, JSONUtils.EMPTY_JSONOBJECT);
		JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
		return returnObject.toString();
	}

	private DataBean getDataObject(TaskWritable task) throws Exception {
		Object idObject = task.get("id");
		String proxyIp = getHost(task);
		ProxyRegionVo tBean = new ProxyRegionVo();
		if (idObject != null) {
			tBean.setId(Long.valueOf(idObject.toString()));
		}
		String url = String.format("https://www.maxmind.com/geoip/v2.1/city/%s?demo=1", proxyIp);
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get);
		System.err.println(html);
		JSONObject regionObject = JSONUtils.getJSONObject(html);
		tBean.setRegionCountry(getCNNameValue(JSONUtils.getJSONObject(regionObject, "country")));
		tBean.setRegionCity(getCNNameValue(JSONUtils.getJSONObject(regionObject, "city")));
		JSONArray subArray = JSONUtils.get(regionObject, "subdivisions");
		String sProvince = (subArray == null || subArray.length() < 1) ? "" : getCNNameValue(subArray.getJSONObject(0));
		String sContinent = getCNNameValue(JSONUtils.getJSONObject(regionObject, "continent"));
		tBean.setRegionName(sContinent + "|" + tBean.getRegionCountry() + "|" + sProvince + "|" + tBean.getRegionCity());
		JSONObject traitObject = JSONUtils.getJSONObject(regionObject, "traits");
		tBean.setIspName(JSONUtils.getString(traitObject, "isp"));
		JSONObject locationObject = JSONUtils.getJSONObject(regionObject, "location");
		tBean.setMapLat(JSONUtils.getString(locationObject, "latitude"));
		tBean.setMapLng(JSONUtils.getString(locationObject, "longitude"));
		DataBean rsBean = new DataBean();
		rsBean.getDataList().add(tBean);
		return rsBean;
	}

	private String getCNNameValue(JSONObject jObject) {
		return getNameValue(jObject, "zh-CN");
	}

	private String getNameValue(JSONObject countryObject, String region) {
		if (countryObject == null) {
			return "";
		}
		JSONObject nameObject = JSONUtils.getJSONObject(countryObject, "names");
		if (nameObject == null) {
			return "";
		}
		return JSONUtils.getString(nameObject, region);
	}

	private String getHost(TaskWritable task) {
		Object ipObject = task.get("proxyHost");
		ipObject = ipObject == null ? task.get("ip") : ipObject;
		String ipString = ipObject.toString();
		int index = ipString.indexOf(".");
		if (index > 0) {
			return ipString;
		} else {
			Long ipLong = Long.valueOf(ipString);
			return InetAddressUtils.inet_ntoa(ipLong);
		}
	}

	private class ProxyRegionVo {
		private Long id;
		private String ispName;
		private Integer mapType = 3;
		private String mapLat;
		private String mapLng;
		private String regionName;
		private String regionCountry;
		private String regionCity;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getIspName() {
			return ispName;
		}

		public void setIspName(String ispName) {
			this.ispName = ispName;
		}

		public Integer getMapType() {
			return mapType;
		}

		public void setMapType(Integer mapType) {
			this.mapType = mapType;
		}

		public String getMapLat() {
			return mapLat;
		}

		public void setMapLat(String mapLat) {
			this.mapLat = mapLat;
		}

		public String getMapLng() {
			return mapLng;
		}

		public void setMapLng(String mapLng) {
			this.mapLng = mapLng;
		}

		public String getRegionName() {
			return regionName;
		}

		public void setRegionName(String regionName) {
			this.regionName = regionName;
		}

		public String getRegionCountry() {
			return regionCountry;
		}

		public void setRegionCountry(String regionCountry) {
			this.regionCountry = regionCountry;
		}

		public String getRegionCity() {
			return regionCity;
		}

		public void setRegionCity(String regionCity) {
			this.regionCity = regionCity;
		}
	}
}
