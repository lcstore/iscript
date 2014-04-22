package com.lezo.iscript.yeam.loader;

public class ClassReloader extends ClassLoader {
	private ClassLoader parent = ClassReloader.class.getClassLoader();

	public Class<?> loadClass(String name, byte[] bytes) throws ClassNotFoundException {
		String clsName = ByteClassUtils.getClassName(bytes);
		if (clsName == null) {
			throw new ClassNotFoundException("Can not found class[" + name + "]");
		}
		Class<?> newClass = defineClass(clsName, bytes, 0, bytes.length);
		return newClass;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.loadClass(name);
	}
}