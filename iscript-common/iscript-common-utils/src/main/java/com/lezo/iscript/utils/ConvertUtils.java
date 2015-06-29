package com.lezo.iscript.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtils {

	@SuppressWarnings("unchecked")
	public static <T> T convertTo(Object value, Class<T> targetClass) throws Exception {
		if (value == null) {
			return null;
		}
		Object destValue = null;
		if (targetClass.isAssignableFrom(value.getClass())) {
			destValue = value;
		} else if (Integer.class.isAssignableFrom(targetClass)) {
			destValue = Integer.valueOf(value.toString());
		} else if (Long.class.isAssignableFrom(targetClass)) {
			destValue = Long.valueOf(value.toString());
		} else if (Double.class.isAssignableFrom(targetClass)) {
			destValue = Double.valueOf(value.toString());
		} else if (Float.class.isAssignableFrom(targetClass)) {
			destValue = Float.valueOf(value.toString());
		} else if (Byte.class.isAssignableFrom(targetClass)) {
			destValue = Byte.valueOf(value.toString());
		} else if (Short.class.isAssignableFrom(targetClass)) {
			destValue = Short.valueOf(value.toString());
		} else if (Date.class.isAssignableFrom(targetClass)) {
			if (value instanceof Long) {
				Long mills = (Long) value;
				destValue = new Date(mills);
			} else if (!(value instanceof Date)) {
				SimpleDateFormat sdf = new SimpleDateFormat();
				destValue = sdf.parse(value.toString());
			}
		} else {
			throw new ClassCastException("can not cast from class:" + value.getClass().getName() + ",to class:"
					+ targetClass.getName());
		}
		return (T) destValue;
	}
}
