package com.lezo.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.pcs.BaiduPCSActionInfo.PCSFileInfoResponse;
import com.baidu.pcs.BaiduPCSClient;
import com.lezo.rest.jos.JosRestClient;

public class JosCategoryFetcher {
	private static Logger log = Logger.getLogger(JosCategoryFetcher.class);
	private static Logger rsLogger = Logger.getLogger(ResultLogger.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String access_token = "3.a1333cd5eebc4a402e706e06b060b60a.2592000.1389019338.4026763474-1552221";
		access_token = "21.87c338a28b227958686b65685ac24808.2592000.1401016644.4026763474-1319196";
		String path = "/apps/emao_doc/jditems/category/";
		BaiduPCSClient pcsClient = new BaiduPCSClient(access_token);
		String accessToken = "21.92410d2604cceab404ebd9e5b6bb6777.2592000.1401016455.4026763474-1319196";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "1503e8459a4b4592a281334c311e6ced";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String serverUrl = "http://gw.api.360buy.com/routerjson";
		String method = "jingdong.ware.product.catelogy.list.get";
		JSONObject argsObject = new JSONObject();
		argsObject.put("isIcon", true);
		argsObject.put("isDescription", true);
		argsObject.put("client", "m");
		List<JSONObject> cidObjList = new ArrayList<JSONObject>();
		JSONObject cObj = new JSONObject();
		cObj.put("catelogyId", 0);
		cObj.put("level", 0);
		cidObjList.add(cObj);
		while (!cidObjList.isEmpty()) {
			int index = 0;
			List<JSONObject> nexObjects = new ArrayList<JSONObject>();
			for (JSONObject cidObject : cidObjList) {
				log.info("index:" + (++index) + "/" + cidObjList.size() + ",arg:" + cidObject.toString());
				argsObject.put("catelogyId", cidObject.get("catelogyId"));
				argsObject.put("level", cidObject.get("level"));
				String result = client.execute(method, argsObject.toString());
				if (result != null) {
					JSONObject rsObject = new JSONObject(result);
					if (rsObject.has("error_response")) {
						log.warn(cidObject.toString() + "," + rsObject.getJSONObject("error_response").toString());
						continue;
					}
					JSONObject rspObject = rsObject.getJSONObject("jingdong_ware_product_catelogy_list_get_responce");
					// .jingdong_ware_product_catelogy_list_get_responce.productCatelogyList.catelogyList
					JSONObject pcListObj = rspObject.getJSONObject("productCatelogyList");
					JSONArray cListArray = pcListObj.getJSONArray("catelogyList");
					rsLogger.info(cListArray.toString());
					for (int i = 0; i < cListArray.length(); i++) {
						JSONObject obj = cListArray.getJSONObject(i);
						if (obj.getInt("level") < 3) {
							JSONObject nextObj = new JSONObject();
							nextObj.put("catelogyId", obj.getString("cid"));
							nextObj.put("level", obj.getInt("level") + 1);
							nexObjects.add(nextObj);
						}
					}
					File temFile = new File("src/main/resources/file.temp");
					FileUtils.writeStringToFile(temFile, cListArray.toString(), "utf-8");
					String target = path + cidObject.getString("catelogyId") + "." + (cidObject.getInt("level"))
							+ ".txt";
					String sourceFile = temFile.getAbsolutePath();
					long start = System.currentTimeMillis();
					PCSFileInfoResponse res = pcsClient.uploadFile(sourceFile, target);
					long cost = System.currentTimeMillis() - start;
					log.info("res.status:" + res.commonFileInfo.path + "," + res.status.message + ",cost:" + cost
							+ "ms");
				}
			}
			cidObjList = nexObjects;
		}

	}

}
