package com.lezo.iscript.yeam.compile;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.lezo.iscript.common.compile.CacheJavaCompiler;
import com.lezo.iscript.common.loader.ClassUtils;
import com.lezo.iscript.yeam.service.ConfigParser;

public class ConfigCompileTest {

	public static void main(String[] args) throws Exception {
		File file = new File("src/test/java/com/lezo/iscript/yeam/config/StringLinker.java");
		String codeSource = FileUtils.readFileToString(file);
		String className = "com.lezo.iscript.yeam.config.StringLinker";
		// System.out.println(src);
		CacheJavaCompiler compiler = CacheJavaCompiler.getInstance();
		Class<?> newClass = compiler.doCompile(className, codeSource);
		ConfigParser parser = (ConfigParser) newClass.newInstance();
		System.out.println(parser.getName());
		compiler.doCompile(className, codeSource);
		parser = (ConfigParser) newClass.newInstance();
		System.out.println(parser.getName());
	}

	@Test
	public void testInClass() throws Exception {
		File file = new File("src/test/java/com/lezo/iscript/yeam/compile/InClass.java");
		String codeSource = FileUtils.readFileToString(file);
		String className = ClassUtils.getClassNameFromJava(codeSource);
		// System.out.println(src);
		CacheJavaCompiler compiler = CacheJavaCompiler.getInstance();
		Class<?> newClass = compiler.doCompile(className, codeSource);
		ConfigParser parser = (ConfigParser) newClass.newInstance();
		System.out.println(parser.getName());
		parser.doParse(null);
	}

	@Test
	public void testHuihuiSign() throws Exception {
		File file = new File("src/test/java/com/lezo/iscript/yeam/config/HuihuiSigner.java");
		String codeSource = FileUtils.readFileToString(file);
		String className = ClassUtils.getClassNameFromJava(codeSource);
		// System.out.println(src);
		CacheJavaCompiler compiler = CacheJavaCompiler.getInstance();
		Class<?> newClass = compiler.doCompile(className, codeSource);
		ConfigParser parser = (ConfigParser) newClass.newInstance();
		System.out.println(parser.getName());
	}
}
