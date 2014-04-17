package com.lezo.iscript.yeam.defend.loader;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class CustomClassLoader extends ClassLoader {
	private Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		File clsFile = findBinaryFile(name);
		if (clsFile != null && clsFile.exists()) {
			byte[] bytes = ClassByteUtils.loadClassData(clsFile);
			return loadClass(name, bytes);
		}
		return super.loadClass(name);
	}

	public Class<?> loadClass(String name, byte[] bytes) throws ClassNotFoundException {
		String clsName = ClassByteUtils.getClassName(bytes);
		if (clsName == null) {
			throw new ClassNotFoundException("Can not found class[" + name + "]");
		}
		String[] nameArray = clsName.split("\\.");
		if (name.endsWith(nameArray[nameArray.length - 1])) {
			if (classMap.containsKey(clsName)) {
				throw new UnsupportedOperationException("dumplicate load class[" + clsName + "]");
			}
			Class<?> newClass = defineClass(clsName, bytes, 0, bytes.length);
			classMap.put(newClass.getName(), newClass);
			return newClass;
		}
		return super.loadClass(name);
	}

	public File findBinaryFile(String name) {
		String clsName = name.replace(".class", "");
		clsName = clsName.replace(".", File.separator);
		clsName += ".class";
		File findFile = new File(clsName);
		if (findFile.exists()) {
			return findFile;
		}
		findFile = null;
		File[] roots = File.listRoots();
		if (roots != null) {
			for (File rootFile : roots) {
				File clsFile = new File(rootFile, clsName);
				if (clsFile.exists()) {
					findFile = clsFile;
					break;
				}
			}
		}
		return findFile;
	}

	public Map<String, Class<?>> getClassMap() {
		return classMap;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (classMap.containsKey(name)) {
			return classMap.get(name);
		}
		return super.findClass(name);
	}
}