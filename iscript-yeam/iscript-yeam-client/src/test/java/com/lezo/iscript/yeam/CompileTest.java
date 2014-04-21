package com.lezo.iscript.yeam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.lezo.iscript.yeam.loader.ClassReloader;
import com.lezo.iscript.yeam.service.ConfigParser;

public class CompileTest {

	@Test
	public void test() throws Exception {
		File file = new File(
				"src/test/java/com/lezo/iscript/yeam/config/StringLinker.java");
		String content = FileUtils.readFileToString(file);
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		InputStream in = new ByteArrayInputStream(content.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		String arguments = file.getName();
		int code = javaCompiler.run(in, out, err, "-encoding", "utf-8");
		if (code == 0) {
			ClassReloader reloader = new ClassReloader();
			Class<?> newClass = reloader.loadClass("", out.toByteArray());
			ConfigParser linkParser = (ConfigParser) newClass.newInstance();
			System.out.println(linkParser.getName());
		} else {

			System.err.println("code=" + code + ",msg:"
					+ new String(err.toByteArray()));
		}
		System.out.println("end");
	}

}
