package com.lezo.iscript.yeam.client.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import org.junit.Test;

import com.lezo.iscript.yeam.defend.dirs.FileZipUtils;

public class TestZipUtils {

	@Test
	public void testZip() throws IOException {
		String folder = "C:/dest/pis";
		String dest = "C:/dest/test.zip";
		System.out.println(URLEncoder.encode(
				"useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull", "UTF-8"));
	}

	@Test
	public void testUnZip() throws IOException {
		String folder = "C:/dest/test.zip";
		String dest = "C:/dest/unZip/";
		FileZipUtils.doUnZip(new File(folder), new File(dest));
	}
}
