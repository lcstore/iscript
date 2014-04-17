package com.lezo.iscript.ocr;

import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lezo.iscript.ocr.filter.BinaryFilter;
import com.lezo.iscript.ocr.filter.DilateFilter;
import com.lezo.iscript.ocr.filter.ErodeFilter;
import com.lezo.iscript.ocr.filter.GrayFilter;

public class ANCCOCRParser {
	private static final Pattern IDENTIFY_CODE_PATTERN = Pattern.compile("^[0-9a-zA-Z]{4}$");

	public static String doParse(String tessPath, BufferedImage image) throws Exception {
		// image = ImageFilterHelper.grayFilter(image);
		// image = ImageFilterHelper.changeGrey(image);
		image = new GrayFilter().doFilter(image);
		image = new BinaryFilter().doFilter(image);
		image = new DilateFilter().doFilter(image);
		image = new ErodeFilter().doFilter(image);
		return OCRParser.doParse(tessPath, image);
	}

	public static boolean isVerifyCode(String code) {
		if (code == null || code.length() < 4) {
			return false;
		}
		Matcher matcher = IDENTIFY_CODE_PATTERN.matcher(code);
		return matcher.find();
	}
}
