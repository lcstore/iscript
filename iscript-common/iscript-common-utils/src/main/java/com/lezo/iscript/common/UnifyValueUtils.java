package com.lezo.iscript.common;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnifyValueUtils {
	private static Logger logger = LoggerFactory.getLogger(UnifyValueUtils.class);

	public static <T> boolean hasUnifyField(Class<T> targetClass) {
		Field[] fields = targetClass.getDeclaredFields();
		if (fields == null) {
			return false;
		}
		for (Field field : fields) {
			UnifyValueAnnotation unifyValueAnnotation = field.getAnnotation(UnifyValueAnnotation.class);
			if (unifyValueAnnotation != null) {
				return true;
			}
		}
		return false;
	}

	public static <T> T unifyObject(T target) throws IllegalAccessException {
		if (target == null) {
			return target;
		}
		Field[] fields = target.getClass().getDeclaredFields();
		if (fields == null) {
			return target;
		}
		for (Field field : fields) {
			UnifyValueAnnotation unifyValueAnnotation = field.getAnnotation(UnifyValueAnnotation.class);
			if (unifyValueAnnotation != null) {
				Object currentValue = FieldUtils.readField(field, target, true);
				if (currentValue == null) {
					Object value = convertTo(field, unifyValueAnnotation.value());
					FieldUtils.writeField(field, target, value);
				}
			}
		}
		return target;
	}

	public static <T> List<T> unifyObjects(List<T> targetList) throws IllegalAccessException {
		if (CollectionUtils.isEmpty(targetList)) {
			return targetList;
		}
		T firstObject = targetList.get(0);
		if (!UnifyValueUtils.hasUnifyField(firstObject.getClass())) {
			return targetList;
		}
		for (T targetObject : targetList) {
			targetObject = UnifyValueUtils.unifyObject(targetObject);
		}
		return targetList;
	}

	public static <T> List<T> unifyQuietly(List<T> targetList) {
		try {
			return unifyObjects(targetList);
		} catch (IllegalAccessException e) {
			logger.warn("unify objects:" + targetList.size() + ",cause:", e);
		}
		return targetList;
	}

	private static Object convertTo(Field field, String toValue) {
		toValue = toValue.trim();
		Type type = field.getGenericType();
		if (TypeUtils.isAssignable(type, String.class)) {
			return toValue;
		} else if (TypeUtils.isAssignable(type, Byte.class)) {
			return Byte.valueOf(toValue);
		} else if (TypeUtils.isAssignable(type, Short.class)) {
			return Short.valueOf(toValue);
		} else if (TypeUtils.isAssignable(type, Integer.class)) {
			return Integer.valueOf(toValue);
		} else if (TypeUtils.isAssignable(type, Float.class)) {
			return Float.valueOf(toValue);
		} else if (TypeUtils.isAssignable(type, Double.class)) {
			return Double.valueOf(toValue);
		} else if (TypeUtils.isAssignable(type, Long.class)) {
			return Long.valueOf(toValue);
		}
		throw new RuntimeException("unkown type:" + type);
	}
}
