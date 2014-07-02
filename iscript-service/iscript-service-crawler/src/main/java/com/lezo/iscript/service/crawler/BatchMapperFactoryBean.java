package com.lezo.iscript.service.crawler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;

public class BatchMapperFactoryBean<T> extends MapperFactoryBean<T> {
	private SqlSessionFactory sqlSessionFactory;
	private static final Set<String> CHECK_METHOD_SET = new HashSet<String>();
	static {
		CHECK_METHOD_SET.add("batchUpdate");
		CHECK_METHOD_SET.add("updateOne");
	}

	@Override
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	@Override
	public T getObject() throws Exception {
		if (hasBatchUpdate()) {
			return newBatchUpdateProxy();
		}
		return super.getObject();
	}

	private T newBatchUpdateProxy() throws Exception {
		T mapperObject = super.getObject();
		return (T) new BatchMapperProxy(mapperObject).newInstance();
	}

	private boolean hasBatchUpdate() {
		Method[] methods = getObjectType().getMethods();
		Map<String, List<Class<?>[]>> methodMap = new HashMap<String, List<Class<?>[]>>();
		for (Method m : methods) {
			if (!CHECK_METHOD_SET.contains(m.getName())) {
				continue;
			}
			List<Class<?>[]> paramList = methodMap.get(m.getName());
			if (paramList == null) {
				paramList = new ArrayList<Class<?>[]>();
				methodMap.put(m.getName(), paramList);
			}
			paramList.add(m.getParameterTypes());
		}
		return methodMap.size() == 2;
	}

	class BatchMapperProxy implements InvocationHandler {
		private Object targetObject;

		public BatchMapperProxy(Object targetObject) {
			super();
			this.targetObject = targetObject;
		}

		public Object newInstance() {
			Class<T> mapperInterface = getObjectType();
			return Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("batchUpdate") && method.isAccessible()) {
				doBatchInvoke(proxy, method, args);
				return null;
			}
			return method.invoke(targetObject, args);
		}

		private void doBatchInvoke(Object proxy, Method method, Object[] args) {
			SqlSession sqlSession = sqlSessionFactory.openSession(false);
			try {
				Method updateOneMethod = getUpdateOne(method);
			} catch (Exception ex) {
				sqlSession.rollback();
			} finally {
				sqlSession.commit();
				sqlSession.close();
			}
		}

		private Method getUpdateOne(Method method) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
