package com.lezo.iscript.common;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

public class UnifyValueUtils {

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
