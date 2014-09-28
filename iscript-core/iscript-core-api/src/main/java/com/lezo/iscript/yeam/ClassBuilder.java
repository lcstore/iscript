package com.lezo.iscript.yeam;

import java.util.concurrent.ConcurrentHashMap;

public class ClassBuilder {
	private static final ConcurrentHashMap<String, Class<?>> classMap = new ConcurrentHashMap<String, Class<?>>();
	public static final String YEAM_CLIENT_NAME = "com.lezo.iscript.yeam.client.YeamClient";

	public static synchronized Class<?> newClass(String name, ClassLoader loader, boolean overwrite)
			throws ClassNotFoundException {
		Class<?> newClass = classMap.get(name);
		ClassLoader nowLoader = loader;
		if (overwrite || newClass == null) {
			newClass = nowLoader.loadClass(name);
			addClass(newClass);
		}
		return newClass;
	}

	public static ClassLoader getClassLoader(String name) {
		Class<?> hasClass = ClassBuilder.findClass(name);
		if (hasClass != null) {
			return hasClass.getClassLoader();
		}
		return null;
	}

	/**
	 * register new class,return old class
	 * 
	 * @param newClass
	 * @return
	 */
	public static synchronized Class<?> addClass(Class<?> newClass) {
		String name = newClass.getName();
		Class<?> oldClass = classMap.get(name);
		if (oldClass != null) {
			ObjectBuilder.deleteObject(name);
		}
		classMap.put(name, newClass);
		return oldClass;
	}

	public static Class<?> findClass(String name) {
		Class<?> newClass = classMap.get(name);
		return newClass;
	}

	public static Object deleteClass(String name) {
		ObjectBuilder.deleteObject(name);
		return classMap.remove(name);
	}

	public static void clear() {
		classMap.clear();
	}

}
