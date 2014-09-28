package com.lezo.iscript.ocr;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


public class OCRParser {
	private final static String LANG_OPTION = "-l";
	private final static String format = "jpg";

	public static String doParse(String tessPath, BufferedImage image) throws Exception {
		File imageFile = new File("image." + format);
		ImageIO.write(image, format, imageFile);
		ProcessBuilder pBuilder = new ProcessBuilder();
		File outputFile = setProcessBuilder(pBuilder, imageFile, tessPath);
		int w = pBuilder.start().waitFor();
		assertSuccess(w);
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsoluteFile()), "UTF-8"));
			while (br.ready()) {
				sb.append(br.readLine() + "\n");
			}
			String result = sb.toString().trim();
			if (ANCCOCRParser.isVerifyCode(result)) {
				ImageIO.write(image, "jpg", new File("src/main/resources/imgs", result + ".jpg"));
			}
			return result;
		} finally {
			if (br != null) {
				br.close();
			}
			imageFile.deleteOnExit();
			outputFile.deleteOnExit();
		}

	}

	private static void assertSuccess(int w) {
		String msg = null;
		switch (w) {
		case 0:
			msg = "success";
			break;
		case 1:
			msg = "Errors accessing files. There may be spaces in your image's filename.";
			break;
		case 29:
			msg = "Cannot recognize the image or its selected region.";
			break;
		case 31:
			msg = "Unsupported image format.";
			break;
		default:
			msg = "Errors occurred.";
		}
		if (!"success".equals(msg)) {
			throw new RuntimeException(msg);
		}

	}

	private static File setProcessBuilder(ProcessBuilder pb, File imageFile, String tessPath) {
		String fileName = "ocr.out";
		File outputFile = new File(imageFile.getParentFile(), fileName + ".txt");
		List<String> cmd = new ArrayList<String>();
		cmd.add(tessPath + File.separator + "tesseract");
		cmd.add("");
		cmd.add(fileName);
		cmd.add(LANG_OPTION);
		cmd.add("iancc");
		cmd.add("-psm 7");
		pb.directory(imageFile.getParentFile());

		cmd.set(1, imageFile.getName());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		return outputFile;
	}

}
