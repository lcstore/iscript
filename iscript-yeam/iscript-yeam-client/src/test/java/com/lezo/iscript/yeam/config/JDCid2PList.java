package com.lezo.iscript.yeam.config;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;
import com.lezo.rest.jos.JosRestClient;

public class JDCid2PList implements ConfigParser {

	@Override
	public String getName() {
		return "jd-c2list";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		int pageSize = 50;
		String sCid = "5021";
		JSONObject argsObject = new JSONObject();
		argsObject.put("catelogyId", sCid);
		argsObject.put("pageSize", pageSize);
		argsObject.put("client", "m");
		int page = 1;
		JSONObject rs = new JSONObject();
		JSONObject oHeadObject = null;
		while (true) {
			JSONObject oPListObject = getPList(argsObject, page++);
			JSONObject scListResObject = JSONUtils.get(oPListObject,
					"jingdong_ware_promotion_search_catelogy_list_responce", JSONObject.class);
			JSONObject scListObject = JSONUtils.get(scListResObject, "searchCatelogyList", JSONObject.class);
			JSONArray oPages = JSONUtils.get(rs, "oPages", JSONArray.class);
			if (oPages == null) {
				oPages = new JSONArray();
				rs.put("oPages", oPages);
			}
			JSONArray wareInfos = JSONUtils.get(scListObject, "wareInfo", JSONArray.class);
			oPages.put(wareInfos);
			if (oHeadObject == null) {
				scListObject.remove("wareInfo");
				oHeadObject = scListObject;
				rs.put("oHeader", oHeadObject);
			}
			if (wareInfos.length() < pageSize) {
				break;
			}
		}
		return rs.toString();
	}

	private JSONObject getPList(JSONObject argsObject, int page) throws Exception {
		argsObject.put("page", page);
		System.out.println("args:" + argsObject);
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "7b7d95759e594b2f89a553b350f3d131";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.promotion.search.catelogy.list";
		String result = client.execute(method, argsObject.toString());
		JSONObject oResObject = new JSONObject(result);
		return oResObject;
	}

}
