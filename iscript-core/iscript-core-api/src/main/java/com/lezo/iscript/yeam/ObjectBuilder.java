package com.lezo.iscript.yeam;

import java.util.concurrent.ConcurrentHashMap;

public class ObjectBuilder {
	private static final ConcurrentHashMap<String, Object> singleObjMap = new ConcurrentHashMap<String, Object>();

	public static synchronized Object newObject(String name, boolean single) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		// TODO： 是否存在多线程问题？
		Object obj = findObject(name);
		if (single && obj != null) {
			return obj;
		} else if (!single && obj != null) {
			throw new IllegalArgumentException("Object[" + name + "] is a single Object,but try to new another object");
		}
		Class<?> newClass = ClassBuilder.findClass(name);
		if (newClass == null) {
			ClassLoader clientLoader = ClassBuilder.getClassLoader(ClassBuilder.YEAM_CLIENT_NAME);
			if (clientLoader == null) {
				throw new ClassNotFoundException(ClassBuilder.YEAM_CLIENT_NAME);
			}
			newClass = ClassBuilder.newClass(name, clientLoader, false);
		}
		obj = newClass.newInstance();
		if (single && obj != null) {
			newObject(name, obj);
		}
		return obj;
	}

	public static Object newObject(String name) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		return newObject(name, true);
	}

	public static Object newObject(Class<?> myClass, boolean single) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		String clsName = getName(myClass);
		return newObject(clsName, single);
	}

	public static Object newObject(String name, Object value) {
		if (singleObjMap.containsKey(name)) {
			throw new IllegalArgumentException("Object[" + name + "] is a single Object,but try to new another object");
		}
		singleObjMap.put(name, value);
		return value;
	}

	public static String getName(Class<?> myClass) {
		String clsName = myClass.getName();
		if (myClass.isInterface()) {
			clsName = myClass.getPackage().getName() + ".impl." + myClass.getSimpleName() + "Impl";
		}
		return clsName;
	}

	public static Object findObject(String name) {
		return singleObjMap.get(name);
	}

	public static Object deleteObject(String name) {
		return singleObjMap.remove(name);
	}

	public static synchronized void clear() {
		singleObjMap.clear();
	}
}
