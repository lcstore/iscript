package com.lezo.iscript.yeam.defend.loader;

import java.util.HashMap;
import java.util.Map;

public class ReloadClassLoader extends ClassLoader {
	private Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
	private CustomClassLoader currentLoader = new CustomClassLoader();

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> newClass = null;
		try {
			if (classMap.containsKey(name)) {
				currentLoader = new CustomClassLoader();
			}
			newClass = currentLoader.loadClass(name);
		} catch (UnsupportedOperationException e) {
			currentLoader = new CustomClassLoader();
			newClass = currentLoader.loadClass(name);
		}
		if (newClass.getClassLoader() instanceof CustomClassLoader) {
			classMap.put(newClass.getName(), newClass);
		}
		return newClass;
	}

	public Class<?> loadClass(String name, byte[] bytes) throws ClassNotFoundException {
		Class<?> newClass = null;
		String clsName = name.replaceFirst("\\.class$", "");
		clsName = clsName.replace("/", ".");
		try {
			if (classMap.containsKey(clsName)) {
				currentLoader = new CustomClassLoader();
			}
			newClass = currentLoader.loadClass(clsName, bytes);
		} catch (UnsupportedOperationException e) {
			currentLoader = new CustomClassLoader();
			newClass = currentLoader.loadClass(clsName, bytes);
		}
		if (newClass.getClassLoader() instanceof CustomClassLoader) {
			classMap.put(newClass.getName(), newClass);
		}
		return newClass;
	}

	public Map<String, Class<?>> getClassMap() {
		return classMap;
	}

	public void setClassMap(Map<String, Class<?>> classMap) {
		this.classMap = classMap;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (classMap.containsKey(name)) {
			return classMap.get(name);
		}
		return super.findClass(name);
	}

}