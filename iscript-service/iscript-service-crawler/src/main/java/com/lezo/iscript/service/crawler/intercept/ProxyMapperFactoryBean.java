package com.lezo.iscript.service.crawler.intercept;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;

public class ProxyMapperFactoryBean<T> extends MapperFactoryBean<T> implements InvocationHandler {
	public static final String DEFAULT_INTERCEPT_METHOD = "batchUpdate";
	private SqlSessionFactory sqlSessionFactory;
	private T targetObject;
	private List<String> invokParams;
	private Map<String, String> methodParamMap;

	public ProxyMapperFactoryBean() {
		methodParamMap = new HashMap<String, String>();
		methodParamMap.put("batchUpdate", "param1");
	}

	public boolean isIntercepter(Method method) {
		return methodParamMap.containsKey(method.getName());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (isIntercepter(method)) {
			return doIntercepter(proxy, method, args);
		}
		return method.invoke(this.targetObject, args);
	}

	@SuppressWarnings("unchecked")
	public Object doIntercepter(Object proxy, Method method, Object[] args) throws Exception {
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		Integer result = 0;
		try {
			String sqlMapper = getObjectType().getName() + "." + method.getName();
			Object paramObject = SqlSessionParamsUtils.doConvert(method, args);
			if (paramObject instanceof Map) {
				Map<String, Object> paramMap = (Map<String, Object>) paramObject;
				String batchKey = methodParamMap.get(method.getName());
				Collection<Object> batchCollection = (Collection<Object>) paramMap.get(batchKey);
				for (Object bObject : batchCollection) {
					paramMap.put(batchKey, bObject);
					result += sqlSession.update(sqlMapper, paramMap);
				}
				sqlSession.commit();
			} else if (paramObject instanceof List) {
				Collection<Object> batchCollection = (Collection<Object>) paramObject;
				for (Object bObject : batchCollection) {
					result += sqlSession.update(sqlMapper, bObject);
				}
				sqlSession.commit();
			} else {
				throw new RuntimeException("It is not an auto batch mapper:[" + sqlMapper + "]");
			}
		} catch (Exception ex) {
			sqlSession.rollback();
			throw ex;
		} finally {
			sqlSession.close();
		}
		return result;
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

	public List<String> getInvokParams() {
		return invokParams;
	}

	public void setInvokParams(List<String> invokParams) {
		methodParamMap.clear();
		for (String mParam : invokParams) {
			int index = mParam.indexOf(":");
			if (index < 0) {
				methodParamMap.put(mParam.trim(), "param1");
			} else {
				String methodName = mParam.substring(0, index);
				String convertParam = mParam.substring(index + 1);
				methodParamMap.put(methodName.trim(), convertParam.trim());
			}
		}
		this.invokParams = invokParams;
	}

}
