package com.lezo.iscript.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.json.JSONObject;

public class ObjectUtils {

	@SuppressWarnings("unchecked")
	public static <T> T newObject(Class<T> clazz, Object... args) throws Exception {
		if (clazz.isInterface()) {
			throw new IllegalArgumentException("[" + clazz.getName() + "].is an interface,can not new object..");
		}
		Constructor<T> ctor = null;
		if (args == null || args.length < 1) {
			ctor = clazz.getDeclaredConstructor();
		} else {
			for (Constructor<?> newCtor : clazz.getConstructors()) {
				Class<?>[] pClsArray = newCtor.getParameterTypes();
				int aLen = args.length;
				int pLen = pClsArray.length;
				if (aLen != pLen) {
					continue;
				}
				boolean bMath = true;
				for (int i = 0; i < pLen; i++) {
					if (args[i] == null) {
						continue;
					}
					// if is subClass
					if (!pClsArray[i].isAssignableFrom(args[i].getClass())
							&& !args[i].getClass().isAssignableFrom(pClsArray[i])) {
						// if is Integer,Float,Long?
						if (args[i] instanceof Number && args[i] instanceof Comparable) {
							Field typeField = args[i].getClass().getField("TYPE");
							if (typeField == null || !Modifier.isStatic(typeField.getModifiers())) {
								bMath = false;
								break;
							}
							// Use static field TYPE to decide
							Object tValue = typeField.get(null);
							if (tValue == null || !pClsArray[i].getSimpleName().equals(tValue.toString())) {
								bMath = false;
								break;
							}
						} else {
							bMath = false;
							break;
						}
					}
				}
				if (bMath) {
					ctor = (Constructor<T>) newCtor;
				}
			}
		}
		return ctor.newInstance(args);
	}

	public static <T> T copyObject(Object source, ValueGetter vGetter, T target) throws Exception {
		BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			Method writeMethod = pd.getWriteMethod();
			if (writeMethod == null) {
				continue;
			}
			if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
				continue;
			}
			if (!vGetter.hasKey(pd.getName())) {
				continue;
			}
			writeMethod.invoke(target, vGetter.getValue(pd.getName(), source));
		}
		return target;
	}

	public static <T> T copyObject(final JSONObject source, T target) throws Exception {
		return copyObject(source, new ValueGetter() {
			@Override
			public boolean hasKey(String name) {
				return !source.isNull(name);
			}

			@Override
			public Object getValue(String name, Object source) {
				JSONObject jObject = (JSONObject) source;
				return JSONUtils.getObject(jObject, name);
			}
		}, target);
	}

	public static <T> T copyObject(final Map<String, Object> source, T target) throws Exception {
		return copyObject(source, new ValueGetter() {
			@Override
			public boolean hasKey(String name) {
				return !source.containsKey(name);
			}

			@Override
			public Object getValue(String name, Object source) {
				@SuppressWarnings("unchecked")
				Map<String, Object> dataMap = (Map<String, Object>) source;
				return dataMap.get(name);
			}
		}, target);
	}

}
