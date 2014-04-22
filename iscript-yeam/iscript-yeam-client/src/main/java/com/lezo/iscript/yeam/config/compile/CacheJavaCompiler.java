package com.lezo.iscript.yeam.config.compile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import com.lezo.iscript.yeam.loader.OverrideClassLoader;
import com.lezo.iscript.yeam.loader.ResourceCacheable;
import com.lezo.iscript.yeam.loader.ResourceManager;

public class CacheJavaCompiler {
	private static CacheJavaCompiler instance;
	private ResourceCacheable resourceManager;
	private OverrideClassLoader overrideClassLoader;
	private String classpath;

	private CacheJavaCompiler() {
		this.classpath = getDefaultClassPath();
		this.resourceManager = new ResourceManager();
		this.overrideClassLoader = new OverrideClassLoader(this.resourceManager);
	}

	public static CacheJavaCompiler getInstance() {
		if (instance == null) {
			synchronized (CacheJavaCompiler.class) {
				if (instance == null) {
					instance = new CacheJavaCompiler();
				}
			}
		}
		return instance;
	}

	private String getDefaultClassPath() {
		ClassLoader loader = CacheJavaCompiler.class.getClassLoader();
		StringBuilder sb = new StringBuilder();
		if (loader instanceof URLClassLoader) {
			@SuppressWarnings("resource")
			URLClassLoader urlClassLoader = (URLClassLoader) loader;
			for (URL url : urlClassLoader.getURLs()) {
				String filePath = url.getFile();
				sb.append(filePath).append(File.pathSeparator);
			}
		}
		return sb.toString();
	}

	public Class<?> doCompile(String className, String codeSource) throws ClassNotFoundException, IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(diagnostics, null, null);
		CacheJavaFileManager fileManager = new CacheJavaFileManager(standardJavaFileManager);
		List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
		jfiles.add(new InputJavaFileObject(className, codeSource));

		List<String> options = new ArrayList<String>();
		options.add("-encoding");
		options.add("UTF-8");
		options.add("-classpath");
		options.add(this.classpath);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, jfiles);
		boolean success = task.call();
		if (success) {
			OutputJavaFileObject ojfObject = (OutputJavaFileObject) fileManager.getJavaFileForOutput(
					StandardLocation.CLASS_OUTPUT, className, Kind.CLASS, null);
			addResource(className, ojfObject);
			Class<?> clazz = overrideClassLoader.loadClass(className);
			return clazz;
		}
		return null;
	}

	private void addResource(String className, OutputJavaFileObject ojfObject) throws IOException {
		OutputStream out = ojfObject.openOutputStream();
		ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
		resourceManager.addResource(className, bos.toByteArray());
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

}
