package com.lezo.iscript.utils;

import java.lang.reflect.Method;

public class MethodUtils {

	public static Method getWriteMethod(String fieldName, Class<?> beanClass, Object... valueObjects) {
		int len = valueObjects.length;
		Class<?>[] paramTypes = new Class<?>[len];
		for (int i = 0; i < len; i++) {
			paramTypes[i] = valueObjects[i].getClass();
		}
		String setMdName = getMethodName("set", fieldName, true);
		Method md = getMethod(setMdName, beanClass, paramTypes);
		if (md == null) {
			setMdName = getMethodName("set", fieldName, false);
			md = getMethod(setMdName, beanClass, paramTypes);
		}
		return md;
	}

	public static Method getWriteMethod(String fieldName, Class<?> beanClass, Class<?>... paramTypes) {
		String setMdName = getMethodName("set", fieldName, true);
		Method md = getMethod(setMdName, beanClass, paramTypes);
		if (md == null) {
			setMdName = getMethodName("set", fieldName, false);
			md = getMethod(setMdName, beanClass, paramTypes);
		}
		return md;
	}

	public static Method getReadMethod(String fieldName, Class<?> beanClass) {
		String setMdName = getMethodName("get", fieldName, true);
		Method md = getMethod(setMdName, beanClass);
		if (md == null) {
			setMdName = getMethodName("get", fieldName, false);
			md = getMethod(setMdName, beanClass);
		}
		return md;
	}

	private static String getMethodName(String prefix, String fieldName, boolean firstUpperCase) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		if (firstUpperCase) {
			sb.append(fieldName.substring(0, 1).toUpperCase());
		} else {
			sb.append(fieldName.substring(0, 1));
		}
		sb.append(fieldName.substring(1));
		return sb.toString();
	}

	public static Method getMethod(String methodName, Class<?> cls, Class<?>... paramTypes) {
		int len = paramTypes.length;
		Method[] mdArray = cls.getDeclaredMethods();
		for (Method md : mdArray) {
			if (!md.getName().equals(methodName)) {
				continue;
			}
			Class<?>[] mParams = md.getParameterTypes();
			if (mParams.length != len) {
				continue;
			}
			boolean match = true;
			int pLen = mParams.length;
			for (int i = 0; i < pLen; i++) {
				if (!paramTypes[i].isAssignableFrom(mParams[i])) {
					match = false;
				}

			}
			if (match) {
				return md;
			}
		}
		return null;
	}

}
