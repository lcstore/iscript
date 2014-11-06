package com.lezo.iscript.service.crawler.intercept;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;

public class ProxyMapperFactoryBean<T> extends MapperFactoryBean<T> implements InvocationHandler {
	public static final String DEFAULT_INTERCEPT_METHOD = "batchUpdate";
	private SqlSessionFactory sqlSessionFactory;
	private T targetObject;
	private List<BatchMethod> batchMethods;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		BatchMethod batchMethod = findBatchMethod(method);
		if (batchMethod != null) {
			return doIntercepter(proxy, method, args, batchMethod);
		}
		return method.invoke(this.targetObject, args);
	}

	private BatchMethod findBatchMethod(Method method) {
		if (CollectionUtils.isEmpty(batchMethods)) {
			return null;
		}
		Class<?>[] pTypes = method.getParameterTypes();
		if (pTypes == null || pTypes.length < 1) {
			return null;
		}
		int pSize = pTypes.length;
		String name = method.getName();
		for (BatchMethod bm : batchMethods) {
			if (name.equals(bm.getName()) && pSize > bm.getIndex()) {
				return bm;
			}
		}
		return null;
	}

	public Object doIntercepter(Object proxy, Method method, Object[] args, BatchMethod batchMethod) throws Exception {
		List<?> dataList = (List<?>) args[0];
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		try {
			String sqlMapper = getObjectType().getName() + "." + method.getName();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = getCommonParamMap(method, args, batchMethod);
			boolean hasParamName = !CollectionUtils.isEmpty(keyList);
			if (hasParamName) {
				for (int i = 1; i < keyList.size(); i++) {
					paramMap.put(keyList.get(i), args[i]);
				}
			}
			for (Object data : dataList) {
				if (!hasParamName) {
					sqlSession.update(sqlMapper, data);
				} else {
					paramMap.put(keyList.get(0), data);
					sqlSession.update(sqlMapper, paramMap);
				}
			}
			sqlSession.commit();
		} catch (Exception ex) {
			sqlSession.rollback();
			throw ex;
		} finally {
			sqlSession.close();
		}
		return null;
	}

	private Map<String, Object> getCommonParamMap(Method method, Object[] args, BatchMethod batchMethod) {
		if (args == null) {
			return null;
		}
		Type[] pTypes = method.getGenericParameterTypes();
		int batchIndex = batchMethod.getIndex();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (int i = 0; i < pTypes.length; i++) {
			if (batchIndex != i) {
				Type pType = pTypes[i];
				paramMap.put(key, value);
			}
		}
		return paramMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		this.targetObject = super.getObject();
		return (T) Proxy.newProxyInstance(targetObject.getClass().getClassLoader(), targetObject.getClass().getInterfaces(), this);
	}

	@Override
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	public List<BatchMethod> getBatchMethods() {
		return batchMethods;
	}

	public void setBatchMethods(List<BatchMethod> batchMethods) {
		this.batchMethods = batchMethods;
	}

}
