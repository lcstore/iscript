package com.lezo.iscript.yeam.client.service.impl;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

import org.junit.Test;

import com.lezo.iscript.client.config.MyObjectInterface;
import com.lezo.iscript.client.config.MyObjectSuperClass;
import com.lezo.iscript.yeam.ClassBuilder;
import com.lezo.iscript.yeam.Clientable;
import com.lezo.iscript.yeam.defend.loader.CustomReloadClassLoader;
import com.lezo.iscript.yeam.defend.loader.ReloadClassLoader;

public class TestReloadClassLoader {

	@Test
	public void testLoader() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		ReloadClassLoader classLoader = new ReloadClassLoader();
		String name = "lezo.iscript.trunk.iscript-core.iscript-core-client.config.com.lezo.iscript.client.config.MyObjectClass";
		Class<?> myObjectClass = classLoader.loadClass(name);

		MyObjectInterface object1 = (MyObjectInterface) myObjectClass.newInstance();

		MyObjectSuperClass object2 = (MyObjectSuperClass) myObjectClass.newInstance();
		object1.say();
		object2.say();
		// create new class loader so classes can be reloaded.

		myObjectClass = classLoader.loadClass(name);

		object1 = (MyObjectInterface) myObjectClass.newInstance();
		object2 = (MyObjectSuperClass) myObjectClass.newInstance();
		object1.say();
		object2.say();
	}

	@Test
	public void testReloader2() throws Exception {
		File curFile = new File("src/test/resources/class");
		URL[] urls = new URL[] { curFile.toURI().toURL() };
		CustomReloadClassLoader classLoader = new CustomReloadClassLoader(urls);
		String name = "com.lezo.iscript.client.config.MyObjectClass";
		Class<?> myObjectClass = classLoader.loadClass(name);

		MyObjectInterface object1 = (MyObjectInterface) myObjectClass.newInstance();

		MyObjectSuperClass object2 = (MyObjectSuperClass) myObjectClass.newInstance();
		object1.say();
		object2.say();
		// create new class loader so classes can be reloaded.

		myObjectClass = classLoader.loadClass(name);

		object1 = (MyObjectInterface) myObjectClass.newInstance();
		object2 = (MyObjectSuperClass) myObjectClass.newInstance();
		object1.say();
		object2.say();
	}

	@Test
	public void testReloaderJar() throws Exception {
		File jarFile = new File("C:/yeam/updatespace/client/client.jar");
		File jar2File = new File("C:/yeam/updatespace/client/crawler.jar");
		URL[] urls = new URL[] { jarFile.toURI().toURL(), jar2File.toURI().toURL() };
		CustomReloadClassLoader classLoader = new CustomReloadClassLoader(urls,
				CustomReloadClassLoader.class.getClassLoader());
		String name = "com.lezo.iscript.yeam.client.YeamClient";
		Class<?> myObjectClass = classLoader.loadClass(name);

		Clientable clientable = (Clientable) myObjectClass.newInstance();
		clientable.startup(null);
	}

	@Test
	public void testLoaderFile() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String path = "src/test/resources/class/";
		ReloadClassLoader classLoader = new ReloadClassLoader();
		Class<?> myObjectClass = classLoader.loadClass(new File(path, "MyObjectClass.class").getAbsolutePath());

		MyObjectInterface object1 = (MyObjectInterface) myObjectClass.newInstance();

		MyObjectSuperClass object2 = (MyObjectSuperClass) myObjectClass.newInstance();
		object1.say();
		object2.say();

		// reload
		myObjectClass = classLoader.loadClass(new File(path, "MyObjectClass.class").getAbsolutePath());
		object1 = (MyObjectInterface) myObjectClass.newInstance();

		object2 = (MyObjectSuperClass) myObjectClass.newInstance();
		object1.say();
		object2.say();
	}

	@Test
	public void testReleaseUrl() throws Exception {
		File jarFile = new File("C:/yeam/client.jar");
		URL[] urls = new URL[] { jarFile.toURI().toURL() };
		CustomReloadClassLoader classLoader = new CustomReloadClassLoader(urls,
				CustomReloadClassLoader.class.getClassLoader());
		ClassBuilder.newClass(ClassBuilder.YEAM_CLIENT_NAME, classLoader, true);
		for (URL u : classLoader.getURLs()) {
			File cFile = new File(u.toURI());
			System.out.println(cFile.canExecute() + "," + cFile.canRead() + "," + cFile.canWrite());
			// RandomAccessFile raf = new RandomAccessFile(cFile, "rw");
			// raf.getChannel().lock().release();
			// raf.close();
			cFile.setExecutable(true);
			cFile.setReadable(true);
			cFile.setWritable(true);
			boolean ss = cFile.delete();
			System.out.println(ss);
		}
	}

	@Test
	public void closeUrl() throws Exception {
		File jarFile = new File("C:/yeam/client.jar");
		URL[] urls = new URL[] { jarFile.toURI().toURL() };
		CustomReloadClassLoader classLoader = new CustomReloadClassLoader(urls,
				CustomReloadClassLoader.class.getClassLoader());
		ClassBuilder.newClass(ClassBuilder.YEAM_CLIENT_NAME, classLoader, true);
		Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
		ucpField.setAccessible(true);
		URL[] list = classLoader.getURLs();
		Object ucpObject = ucpField.get(classLoader);
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
			}
		}
		ClassBuilder.newClass(ClassBuilder.YEAM_CLIENT_NAME, classLoader, true);
	}
}