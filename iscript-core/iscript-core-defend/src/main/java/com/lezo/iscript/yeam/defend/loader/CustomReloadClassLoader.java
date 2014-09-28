package com.lezo.iscript.yeam.defend.loader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarFile;

import sun.misc.ClassLoaderUtil;

import com.lezo.iscript.yeam.ClassBuilder;

public class CustomReloadClassLoader extends URLClassLoader {
	private AtomicBoolean reloadable = new AtomicBoolean(false);

	public CustomReloadClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public CustomReloadClassLoader(URL[] urls) {
		super(urls);
	}

	public void addNewURL(URL newUrl) {
		URL[] urls = super.getURLs();
		for (URL url : urls) {
			if (url.equals(newUrl)) {
				return;
			}
		}
		super.addURL(newUrl);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loadClass(name, false);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		ClassLoader loader = ClassBuilder.getClassLoader(name);
		if (loader != null && getReloadable()) {
			ClassBuilder.deleteClass(name);
			return reloadClass(name, resolve);
		}
		Class<?> newClass = super.loadClass(name, resolve);
		if (resolve) {
			super.resolveClass(newClass);
		}
		ClassBuilder.addClass(newClass);
		return newClass;
	}

	private Class<?> reloadClass(String name, boolean resolve) throws ClassNotFoundException {
		CustomReloadClassLoader newLoader = new CustomReloadClassLoader(super.getURLs(), super.getParent());
		return newLoader.loadClass(name, resolve);
	}

	public int closeURLs() throws Exception {
		int closeNum = 0;
		Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
		ucpField.setAccessible(true);
		URL[] list = this.getURLs();
		Object ucpObject = ucpField.get(this);
		for (int i = 0; i < list.length; i++) {
			Method m = ucpObject.getClass().getDeclaredMethod("getLoader", int.class);
			m.setAccessible(true);
			Object jarLoader = m.invoke(ucpObject, i);
			String clsName = jarLoader.getClass().getName();
			if (clsName.indexOf("JarLoader") > -1) {
				m = jarLoader.getClass().getDeclaredMethod("ensureOpen");
				m.setAccessible(true);
				m.invoke(jarLoader);
				m = jarLoader.getClass().getDeclaredMethod("getJarFile");
				m.setAccessible(true);
				JarFile jf = (JarFile) m.invoke(jarLoader);
				jf.close();
				closeNum++;
			}
		}
		return closeNum;
	}

	public boolean getReloadable() {
		return reloadable.get();
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable.set(reloadable);
	}
}
