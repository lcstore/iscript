package com.lezo.iscript.yeam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import com.lezo.iscript.yeam.config.compile.OutputJavaFileObject;

public class DynamicClassLoader extends URLClassLoader {
	public DynamicClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
	}

	public Class<?> findClassByClassName(String className) throws ClassNotFoundException {
		return this.findClass(className);
	}

	public Class<?> loadClass(String fullName, OutputJavaFileObject jco) {
		ByteArrayOutputStream bos = null;
		try {
			bos = (ByteArrayOutputStream) jco.openOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] classData = bos.toByteArray();
		return this.defineClass(fullName, classData, 0, classData.length);
	}
}
