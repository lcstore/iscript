/*
 * OCR.java
 *
 * Created on December 24, 2007, 12:38 AM
 */

package com.lezo.iscript.ocr.tesseract;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.log4j.Logger;

import com.lezo.iscript.ocr.ANCCVerifyCodeParser;

public class OCR {
	protected static final Logger logger = Logger.getLogger(OCR.class);
	private final static String LANG_OPTION = "-l";
	private final String EOL = System.getProperty("line.separator");
	// private static final String tessPath = new
	// File("src/main/resources/tesseract").getAbsolutePath();
	public static final String tessPath = "D:/lezo/iscript/v0.01/iscript-core/iscript-core-ocr/src/main/resources/tesseract";

	// private static final String tessPath = new
	// File("C:/Program Files/Tesseract-OCR").getAbsolutePath();

	public static void main(String[] args) throws IOException {
		File imageFile = new File("r42l.jpg");
		imageFile = new File("D:/lezo/iscript/v0.01/iscript-core/iscript-core-crawler/R42L.jpg");
		// imageFile = new File("42R6-binary.jpg");
		// imageFile = new File("42R6-gray.jpg");
		// imageFile = new File("bcode.jpg");
		String imageFormat = "jpg";
		// File imageFile = new File("12345.png");
		// String imageFormat = "png";

		BufferedImage bi = ImageIO.read(imageFile);
		bi = ImageFilterHelper.grayFilter(bi);
		bi = ImageFilterHelper.changeGrey(bi);
		// File output = new File("buf.jpg");
		// ImageIO.write(bi, imageFormat, output);
		try {
			// String result = OCR.recognizeText(output, imageFormat);
			String result = ANCCVerifyCodeParser.doParse(bi);
			System.out.println(imageFile.getName() + " : " + result);
			// logger.info("图像识别结果:" + result);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn(imageFile.getName() + ",cause:", e);
		}
		// doBatchRecognize();
	}

	public static void doBatchRecognize() {
		OCR ocr = new OCR();
		final String imageFormat = "jpg";
		File curFile = new File(".");
		File[] jpgFileArr = curFile.listFiles(new FileFilter() {
			public boolean accept(File args) {
				return (args.isFile() && args.getName().endsWith("." + imageFormat));
			}
		});
		for (File file : jpgFileArr) {

			try {
				String result = OCR.recognizeText(file, imageFormat);
				System.out.println(file.getName() + " : " + result);
				// logger.info("图像识别结果:" + result);
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println(file.getName() + " : error");
			}
		}
	}

	public static void write(File destFile, BufferedImage bufImg) {
		String format = destFile.getName();
		int index = format.lastIndexOf(".");
		format = format.substring(index + 1);
		try {
			ImageIO.write(bufImg, format, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String recognizeText(File imageFile, String imageFormat) throws Exception {
		File tempImage = ImageIOHelper.createImage(imageFile, imageFormat);

		File outputFile = new File(imageFile.getParentFile(), "output");
		StringBuffer strB = new StringBuffer();

		List<String> cmd = new ArrayList<String>();
		cmd.add(tessPath + File.separator + "tesseract");
		cmd.add("");
		cmd.add(outputFile.getName());
		cmd.add(LANG_OPTION);
		cmd.add("eng");
		cmd.add("-psm 7");

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(imageFile.getParentFile());

		cmd.set(1, tempImage.getName());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		Process process = pb.start();

		int w = process.waitFor();
		// logger.debug("Exit value = " + w);
		// delete temp working files
		tempImage.delete();

		if (w == 0) {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(
					outputFile.getAbsolutePath() + ".txt"), "UTF-8"));

			String str;

			while ((str = in.readLine()) != null) {
				strB.append(str);
				break;
			}
			in.close();
		} else {
			String msg;
			switch (w) {
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
			tempImage.delete();
			throw new RuntimeException(msg);
		}

		new File(outputFile.getAbsolutePath() + ".txt").delete();
		return strB.toString();
	}

}
