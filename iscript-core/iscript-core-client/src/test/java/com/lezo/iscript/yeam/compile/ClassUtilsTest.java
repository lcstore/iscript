package com.lezo.iscript.yeam.compile;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.lezo.iscript.common.loader.ClassUtils;

public class ClassUtilsTest {

	@Test
	public void test() throws IOException {
		File file = new File("src/test/java/com/lezo/iscript/yeam/config/StringLinker.java");
		String codeSource = FileUtils.readFileToString(file);
		String className = "com.lezo.iscript.yeam.config.StringLinker";
		String javaClsName = ClassUtils.getClassNameFromJava(codeSource);
		System.out.println(javaClsName);

	}
}
