package com.lezo.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.pcs.BaiduPCSActionInfo.PCSFileInfoResponse;
import com.baidu.pcs.BaiduPCSClient;
import com.lezo.rest.jos.JosRestClient;

public class JosSkuFetcher {
	private static Logger log = Logger.getLogger(JosSkuFetcher.class);
	private static Logger rsLogger = Logger.getLogger(ResultLogger.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String access_token = "3.a1333cd5eebc4a402e706e06b060b60a.2592000.1389019338.4026763474-1552221";
		String path = "/apps/emao_doc/jditems/skus/";
		BaiduPCSClient pcsClient = new BaiduPCSClient(access_token);

		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "1503e8459a4b4592a281334c311e6ced";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String serverUrl = "http://gw.api.360buy.com/routerjson";
		client.getConfig().setServerUrl(serverUrl);
		String method = "jingdong.ware.promotion.search.catelogy.list";
		JSONObject argsObject = new JSONObject();
		int pageSize = 100;
		argsObject.put("catelogyId", "9941");
		argsObject.put("page", 1);
		argsObject.put("pageSize", pageSize);
		argsObject.put("client", "m");
		List<JSONObject> cidObjList = new ArrayList<JSONObject>();
		JSONObject cObj = new JSONObject();
		cObj.put("catelogyId", "9716");
		cObj.put("page", 1);
		cidObjList.add(cObj);
		cObj = new JSONObject();
		cObj.put("catelogyId", "9717");
		cObj.put("page", 1);
		cidObjList.add(cObj);
		cObj.put("catelogyId", "9718");
		cObj.put("page", 1);
		cidObjList.add(cObj);
		int total = 0;
		for (JSONObject pageObject : cidObjList) {
			int index = 0;
			int sumSku = 0;
			while (true) {
				log.info("index:" + (++index) + "/" + (++total) + ",size:" + cidObjList.size() + ","
						+ pageObject.toString());
				argsObject.put("catelogyId", pageObject.get("catelogyId"));
				argsObject.put("page", pageObject.get("page"));
				int retry = 3;
				JSONObject rsObject = null;
				while ((--retry) > 0) {
					try {
						Random random = new Random();
						long timeout = random.nextInt(1000);
						timeout += 100;
						log.info(pageObject.toString() + ",sleep:" + timeout);
						TimeUnit.MILLISECONDS.sleep(timeout);
						String result = client.execute(method, argsObject.toString());
						rsObject = new JSONObject(result);
						if (rsObject.has("error_response")) {
							log.warn(pageObject.toString() + "," + rsObject.getJSONObject("error_response").toString());
						} else {
							break;
						}
					} catch (Exception e) {
						log.warn(pageObject.toString() + ",", e);
					}
				}
				// .jingdong_ware_promotion_search_catelogy_list_responce.searchCatelogyList.wareCount
				JSONObject rspObject = rsObject.getJSONObject("jingdong_ware_promotion_search_catelogy_list_responce");
				// .jingdong_ware_promotion_search_catelogy_list_responce.searchCatelogyList.wareInfo
				JSONObject pcListObj = rspObject.getJSONObject("searchCatelogyList");
				JSONArray wareInfo = pcListObj.getJSONArray("wareInfo");
				sumSku += wareInfo.length();
				int wareCount = pcListObj.getInt("wareCount");
				rsLogger.info(wareInfo.toString());
				File temFile = new File("src/main/resources/file.temp");
				FileUtils.writeStringToFile(temFile, wareInfo.toString(), "utf-8");
				String target = path + pageObject.getString("catelogyId") + "_page_" + (pageObject.getInt("page"))
						+ "-" + wareInfo.length() + "-" + wareCount + ".txt";
				String sourceFile = temFile.getAbsolutePath();
				long start = System.currentTimeMillis();
				pcsClient.deleteFile(target);
				PCSFileInfoResponse res = pcsClient.uploadFile(sourceFile, target);
				long cost = System.currentTimeMillis() - start;

				log.info("sku:" + sumSku + "/" + wareCount + "," + pageObject.toString());
				log.info("res.status:" + res.commonFileInfo.path + "," + res.status.message + ",cost:" + cost + "ms");
				if (wareInfo.length() >= pageSize) {
					pageObject.put("page", pageObject.getInt("page") + 1);
				} else {
					break;
				}
			}
		}

	}

}
