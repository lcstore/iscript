package com.lezo.iscript.service.crawler.intercept;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.FactoryBean;

public class IntercepterProxy<T> implements InvocationHandler, InterceptHandler, FactoryBean<T> {
	public static final String DEFAULT_INTERCEPT_METHOD = "batchUpdate";
	private SqlSessionFactory sqlSessionFactory;
	private Object targetObject;
	private String methodName = DEFAULT_INTERCEPT_METHOD;
	private List<String> keyList;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (isIntercept(method)) {
			return doIntercepter(proxy, method, args);
		}
		return method.invoke(this.targetObject, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) Proxy.newProxyInstance(targetObject.getClass().getClassLoader(), targetObject.getClass()
				.getInterfaces(), this);
	}

	@Override
	public Class<?> getObjectType() {
		Class<?>[] interfaces = targetObject.getClass().getInterfaces();
		return interfaces[0];
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Object doIntercepter(Object proxy, Method method, Object[] args) throws Exception {
		List<?> dataList = (List<?>) args[0];
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		try {
			String sqlMapper = getObjectType().getName() + ".updateOne";
			Map<String, Object> paramMap = new HashMap<String, Object>();
			boolean hasParamName = !CollectionUtils.isEmpty(keyList);
			for (Object data : dataList) {
				if (!hasParamName) {
					sqlSession.update(sqlMapper, data);
				} else {
					paramMap.put(keyList.get(0), data);
					for (int i = 1; i < keyList.size(); i++) {
						paramMap.put(keyList.get(i), args[i]);
					}
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

	@Override
	public boolean isIntercept(Method method) {
		Class<?>[] pTypes = method.getParameterTypes();
		if (pTypes == null || pTypes.length < 1) {
			return false;
		}
		return method.getName().equals(this.methodName) && List.class.isAssignableFrom(pTypes[0]);
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public void setTargetObject(T targetObject) {
		this.targetObject = targetObject;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setKeyList(List<String> keyList) {
		this.keyList = keyList;
	}

}
