package com.lezo.iscript.service.crawler.mybatis;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年11月7日
 */
public class SqlSessionParamsUtils {

	public static Object doConvert(Method method, Object[] args) {
		Map<Integer, String> annotationNameMap = getParamNameFromAnnotation(method);
		SortedMap<Integer, String> params = getParams(method, annotationNameMap);
		final int paramCount = params.size();
		if (args == null || paramCount == 0) {
			return null;
		} else if (annotationNameMap.isEmpty() && paramCount == 1) {
			return args[0];
		} else {
			final Map<String, Object> param = new ParamMap<Object>();
			int i = 0;
			for (Map.Entry<Integer, String> entry : params.entrySet()) {
				param.put(entry.getValue(), args[entry.getKey()]);
				// issue #71, add param names as param1, param2...but ensure backward compatibility
				final String genericParamName = "param" + String.valueOf(i + 1);
				if (!param.containsKey(genericParamName)) {
					param.put(genericParamName, args[entry.getKey()]);
				}
				i++;
			}
			return param;
		}
	}

	private static Map<Integer, String> getParamNameFromAnnotation(Method method) {
		Map<Integer, String> paramNameMap = new HashMap<Integer, String>();
		final Object[][] annosArray = method.getParameterAnnotations();
		for (int i = 0; i < annosArray.length; i++) {
			final Object[] paramAnnos = method.getParameterAnnotations()[i];
			for (Object paramAnno : paramAnnos) {
				if (paramAnno instanceof Param) {
					String paramName = ((Param) paramAnno).value();
					paramNameMap.put(i, paramName);
					break;
				}
			}
		}
		return paramNameMap;
	}

	private static SortedMap<Integer, String> getParams(Method method, Map<Integer, String> annotationNameMap) {
		final SortedMap<Integer, String> params = new TreeMap<Integer, String>();
		final Class<?>[] argTypes = method.getParameterTypes();
		for (int i = 0; i < argTypes.length; i++) {
			if (!RowBounds.class.isAssignableFrom(argTypes[i]) && !ResultHandler.class.isAssignableFrom(argTypes[i])) {
				String paramName = String.valueOf(params.size());
				if (annotationNameMap.containsKey(i)) {
					paramName = annotationNameMap.get(i);
				}
				params.put(i, paramName);
			}
		}
		return params;
	}
}
