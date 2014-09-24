package com.lezo.iscript.yeam.resultmgr.handler;

import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.ObjectUtils;

public class BeanCopyDataHandler extends AbstractDataHandler {

	@Override
	protected void doHanlde(String type, JSONObject gObject) throws Exception {
		// {
		// "target:"{"handler":"ProductDataHandler","class":""},
		// "data:"{},
		// "nexts":[],
		// }

		JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
		JSONObject tObject = JSONUtils.getJSONObject(rsObject, "target");
		String clsName = JSONUtils.getString(tObject, "class");
		Class<?> dtoClass = getDtoClass(clsName);
		JSONObject dataObject = JSONUtils.getJSONObject(rsObject, "data");
		if (dataObject == null) {
			return;
		}
		Object dtoObject = ObjectUtils.newObject(dtoClass);
		ObjectUtils.copyObject(dataObject, dtoObject);
		// XXX send to dest writer..
	}

	private Class<?> getDtoClass(String clsName) {
		// TODO Auto-generated method stub
		return null;
	}

}
