package com.lezo.iscript.yeam.loader;

public class ClassReloader extends ClassLoader {

	public Class<?> loadClass(String name, byte[] bytes) throws ClassNotFoundException {
		String clsName = ClassByteUtils.getClassName(bytes);
		if (clsName == null) {
			throw new ClassNotFoundException("Can not found class[" + name + "]");
		}
		Class<?> newClass = defineClass(clsName, bytes, 0, bytes.length);
		return newClass;
	}
}