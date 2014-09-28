package com.lezo.iscript.yeam.crawler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class BarCodeUtilsTest {
	@Test
	public void testIsBarCode() {
		Assert.assertEquals(true, BarCodeUtils.isBarCode("9771671216014"));
		Assert.assertEquals(false, BarCodeUtils.isBarCode("6925303722563"));
		Assert.assertEquals(true, BarCodeUtils.isBarCode("6925303722562"));
		Assert.assertEquals(false, BarCodeUtils.isBarCode("692530B722562"));
		Assert.assertEquals(false, BarCodeUtils.isBarCode("925303722562"));
		Assert.assertEquals(true, BarCodeUtils.isBarCode("8801111186100"));
		Assert.assertEquals(true, BarCodeUtils.isBarCode("9555296408395"));
	}

	@Test
	public void testCheckBarCode() throws Exception {
		List<String> lineList = FileUtils.readLines(new File("src/test/resources/barCode.txt"));
		for (String line : lineList) {
			String[] splitStrings = line.split("\t");
			if (!BarCodeUtils.isBarCode(splitStrings[1])) {
				System.out.println(splitStrings[0]);
			}
		}
	}

}
