package com.lezo.iscript.yeam.defend.loader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class ClassByteUtils {
	private static final Pattern SRC_REG = Pattern.compile("SourceFile.*?(([A-Z]{1}[0-9a-zA-Z]+))\\.java");
	private static List<Pattern> namePatterns = new ArrayList<Pattern>();
	static {
		namePatterns.add(Pattern.compile("LocalVariableTable.*?this.*?([a-z]{1}([0-9a-zA-Z]+/)+([A-Z][0-9a-zA-Z$]+))"));
		namePatterns.add(Pattern.compile("SourceFile.*?.java.*?([a-z]{1}([0-9a-zA-Z]+/)+([A-Z][0-9a-zA-Z$]+))"));
	}

	public static byte[] loadClassData(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(in);
			byte[] bytes = new byte[1024];
			int len = 0;
			// len != bytes.length
			while ((len = bis.read(bytes)) > 0) {
				bos.write(bytes, 0, len);
			}
			bos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bos);
		}
	}

	public static byte[] loadClassData(File clsFile) throws ClassNotFoundException {
		InputStream in = null;
		try {
			in = new FileInputStream(clsFile);
			return loadClassData(in);
		} catch (IOException e) {
			throw new ClassNotFoundException("class[" + clsFile.getPath() + "] not found.", e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	// get class name from bytes
	public static String getClassName(byte[] bytes) {
		String sHead = "";
		try {
			sHead = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		Matcher matcher = SRC_REG.matcher(sHead);
		if (matcher.find(1)) {
			String javaName = matcher.group(1);
			// think about inner class.ResultsHolder$InstanceHolder
			for (Pattern p : namePatterns) {
				matcher = p.matcher(sHead);
				if (matcher.find(1)) {
					String clsName = matcher.group(1).replace("/", ".");
					if (clsName.indexOf(javaName) > -1) {
						return clsName;
					}
				}
			}
		}
		return null;
	}
}