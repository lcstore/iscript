package com.lezo.iscript.yeam.crawler;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mybatis.spring.SqlSessionFactoryBean;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;
import com.lezo.rest.jos.JosRestClient;

public class JDCid2PList implements ConfigParser {
	private static Logger log = Logger.getLogger(JDCid2PList.class);

	public String getName() {
		return "jd-c2list";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject rs = new JSONObject();
		JSONUtils.put(rs, "args", JSONUtils.getJSONObject(task.getArgs()));
		int pageSize = 50;
		String sCid = "5021";
		JSONObject argsObject = new JSONObject();
		argsObject.put("catelogyId", sCid);
		argsObject.put("pageSize", pageSize);
		argsObject.put("client", "m");
		Integer pageArgs = getInteger(task.getArgs(), "page");
		int page = pageArgs == null || pageArgs < 1 ? 1 : pageArgs;
		JSONObject oPListObject = getPList(argsObject, page);
		JSONObject scListResObject = JSONUtils.get(oPListObject,
				"jingdong_ware_promotion_search_catelogy_list_responce");
		JSONObject scListObject = JSONUtils.get(scListResObject, "searchCatelogyList");
		JSONArray wareInfos = JSONUtils.get(scListObject, "wareInfo");
		JSONUtils.put(rs, "oList", wareInfos);
		scListObject.remove("wareInfo");
		JSONObject oHeadObject = scListObject;
		rs.put("oHeader", oHeadObject);
		if (wareInfos.length() < pageSize) {
		} else {
			JSONObject oNextObject = new JSONObject();
			oNextObject.put("page", page + 1);
			rs.put("next", oNextObject);
		}SqlSession sqlSession=null;
		SqlSessionFactoryBean sqlSessionFactoryBean=null;
		SqlSessionFactory factory =null;
		return rs.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Map<String, Object> map, String key, Class<T> typeClass) {
		if (key == null) {
			return null;
		}
		Object valueObject = map.get(key);
		if (valueObject == null) {
			return null;
		}
		return (T) valueObject;
	}

	public static Integer getInteger(Map<String, Object> map, String key) {
		return get(map, key, Integer.class);
	}

	private JSONObject getPList(JSONObject argsObject, int page) throws Exception {
		argsObject.put("page", page);
		log.info("args:" + argsObject);
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
