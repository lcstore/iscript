package com.lezo.iscript.service.crawler.intercept;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;

public class UpdateInterceptHandler implements InterceptHandler {
	private SqlSessionFactory sqlSessionFactory;
	private List<String> keyList;

	@Override
	public Object doIntercepter(Object proxy, Method method, Object[] args) {
		if (!isIntercept(method)) {
			return null;
		}
		List<?> dataList = (List<?>) args[0];
		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (Object data : dataList) {
			paramMap.put(keyList.get(0), data);
			for (int i = 1; i < keyList.size(); i++) {
				paramMap.put(keyList.get(i), args[i]);
			}
		}
		return null;
	}

	@Override
	public boolean isIntercept(Method method) {
		Class<?>[] pTypes = method.getParameterTypes();
		if (pTypes == null || pTypes.length < 1) {
			return false;
		}
		return method.getName().equals("batchUpdate") && List.class.isAssignableFrom(pTypes[0]);
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public void setKeyList(List<String> keyList) {
		this.keyList = keyList;
	}
}
