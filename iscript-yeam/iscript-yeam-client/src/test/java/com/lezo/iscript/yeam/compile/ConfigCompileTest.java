package com.lezo.iscript.yeam.compile;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.lezo.iscript.yeam.config.compile.CacheJavaCompiler;
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
	}
}
