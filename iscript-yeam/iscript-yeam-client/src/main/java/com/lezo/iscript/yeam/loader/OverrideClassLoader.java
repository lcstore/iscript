package com.lezo.iscript.yeam.loader;


public class OverrideClassLoader extends ClassLoader {
	private ClassLoader parent;
	private ResourceCacheable resourceCacheable;

	public OverrideClassLoader(ResourceCacheable resourceCacheable) {
		this(OverrideClassLoader.class.getClassLoader(), resourceCacheable);
	}

	public OverrideClassLoader(ClassLoader parent, ResourceCacheable resourceCacheable) {
		super();
		this.parent = parent;
		this.resourceCacheable = resourceCacheable;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		byte[] resBytes = resourceCacheable.findResource(name);
		if (resBytes == null) {
			return parent.loadClass(name);
		}
		Class<?> hasClass = findLoadedClass(name);
		if (hasClass != null) {
			// reload
			OverrideClassLoader overrideLoader = new OverrideClassLoader(parent, resourceCacheable);
			return overrideLoader.loadClass(name);
		}
		byte[] bytes = resBytes;
		Class<?> newClass = defineClass(name, bytes, 0, bytes.length);
		return newClass;
	}
}