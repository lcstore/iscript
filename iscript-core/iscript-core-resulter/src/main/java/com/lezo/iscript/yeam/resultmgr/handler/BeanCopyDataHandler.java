package com.lezo.iscript.yeam.resultmgr.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.ObjectUtils;
import com.lezo.iscript.yeam.resultmgr.writer.BeanWriterManager;

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
		if (dtoObject == null) {
			return;
		}
		ObjectUtils.copyObject(dataObject, dtoObject);
		ObjectWriter<Object> writer = BeanWriterManager.getInstance().getWriter(dtoObject.getClass().getSimpleName());
		List<Object> dataList = new ArrayList<Object>(1);
		dataList.add(dtoObject);
		writer.write(dataList);
	}

	private Class<?> getDtoClass(String name) throws ClassNotFoundException {
		return Thread.currentThread().getContextClassLoader().loadClass(name);
	}

}
