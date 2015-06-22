package com.lezo.iscript.service.crawler.mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;

import com.lezo.iscript.common.Batch;

public class CustomMapperFactoryBean<T> extends MapperFactoryBean<T> implements InvocationHandler {
    private ConcurrentHashMap<Method, String> batchMethodParamMap = new ConcurrentHashMap<Method, String>();
    private T targetObject;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String batchKey = getBatchKey(method);
        if (StringUtils.isNotBlank(batchKey)) {
            return doIntercepter(proxy, method, args, batchKey);
        }
        return method.invoke(this.targetObject, args);
    }

    private String getBatchKey(Method method) {
        String batchKey = batchMethodParamMap.get(method);
        if (batchKey != null) {
            return batchKey;
        }
        Map<Integer, String> batchParams = getBatchParamName(method);
        if (batchParams.isEmpty()) {
            return null;
        } else if (batchParams.size() > 1) {
            throw new IllegalArgumentException("Batch param must be only one.but<" + batchParams.size() + ",method:"
                    + method.getName());
        }
        Map<Integer, String> params = getParamName(method);
        for (Entry<Integer, String> entry : batchParams.entrySet()) {
            if (params.containsKey(entry.getKey())) {
                batchKey = params.get(entry.getKey());
            } else {
                batchKey = entry.getValue();
            }
            break;
        }
        batchMethodParamMap.put(method, batchKey);
        return batchKey;
    }

    private static Map<Integer, String> getBatchParamName(Method method) {
        Map<Integer, String> paramNameMap = new HashMap<Integer, String>();
        final Object[][] annosArray = method.getParameterAnnotations();
        for (int i = 0; i < annosArray.length; i++) {
            final Object[] paramAnnos = annosArray[i];
            for (Object paramAnno : paramAnnos) {
                if (paramAnno instanceof Batch) {
                    paramNameMap.put(i, "param" + (i + 1));
                }
            }
        }
        return paramNameMap;
    }

    private static Map<Integer, String> getParamName(Method method) {
        Map<Integer, String> paramNameMap = new HashMap<Integer, String>();
        final Object[][] annosArray = method.getParameterAnnotations();
        for (int i = 0; i < annosArray.length; i++) {
            final Object[] paramAnnos = annosArray[i];
            for (Object paramAnno : paramAnnos) {
                if (paramAnno instanceof Param) {
                    Param pAnno = (Param) paramAnno;
                    paramNameMap.put(i, pAnno.value());
                }
            }
        }
        return paramNameMap;
    }

    @SuppressWarnings("unchecked")
    public Object doIntercepter(Object proxy, Method method, Object[] args, String batchKey) throws Exception {
        SqlSessionTemplate session = (SqlSessionTemplate) getSqlSession();
        SqlSessionFactory sqlSessionFactory = session.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        Integer result = 0;
        try {
            String sqlMapper = getObjectType().getName() + "." + method.getName();
            Object paramObject = SqlSessionParamsUtils.doConvert(method, args);
            if (paramObject instanceof Map) {
                Map<String, Object> paramMap = (Map<String, Object>) paramObject;
                Collection<Object> batchCollection = (Collection<Object>) paramMap.get(batchKey);
                for (Object bObject : batchCollection) {
                    paramMap.put(batchKey, bObject);
                    result += sqlSession.update(sqlMapper, paramMap);
                }
                sqlSession.commit(true);
            } else if (paramObject instanceof List) {
                Collection<Object> batchCollection = (Collection<Object>) paramObject;
                for (Object bObject : batchCollection) {
                    result += sqlSession.update(sqlMapper, bObject);
                }
                sqlSession.commit(true);
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
        return (T) Proxy.newProxyInstance(targetObject.getClass().getClassLoader(), targetObject.getClass()
                .getInterfaces(), this);
    }

}
