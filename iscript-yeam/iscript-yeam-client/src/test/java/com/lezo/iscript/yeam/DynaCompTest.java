package com.lezo.iscript.yeam;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.lezo.iscript.yeam.service.ConfigParser;

public class DynaCompTest {
	public static void main(String[] args) throws Exception {
		String fullName = "DynaClass";
		StringBuilder src = new StringBuilder();
		src.append("public class DynaClass {\n");
		src.append("    public String toString() {\n");
		src.append("        return \"Hello, I am \" + ");
		src.append("this.getClass().getSimpleName();\n");
		src.append("    }\n");
		src.append("}\n");

		File file = new File(
				"src/test/java/com/lezo/iscript/yeam/config/StringLinker.java");
		String content = FileUtils.readFileToString(file);
		fullName = "com.lezo.iscript.yeam.config.StringLinker";
		// System.out.println(src);
		DynamicEngine de = DynamicEngine.getInstance();
		Object instance = de.javaCodeToObject(fullName, content);
		ConfigParser parser = (ConfigParser) instance;
		System.out.println(parser.getName());
	}
}
