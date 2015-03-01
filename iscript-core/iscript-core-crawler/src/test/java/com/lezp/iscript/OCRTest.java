package com.lezp.iscript;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.lezo.iscript.ocr.ANCCOCRParser;

public class OCRTest {

	private static final String LANG_OPTION = "-l";

	@Test
	public void test() throws Exception {
		File imageFile = new File("r42l.jpg");
		imageFile = new File("src/main/resources/", "R42L.jpg");
		imageFile = new File("src/main/resources/", "4tv2.jpg");
		BufferedImage bi = ImageIO.read(imageFile);
		int minY = 0;
		int minX = 0;

		BufferedImage srcImg = bi;
		int height = srcImg.getHeight();
		int width = srcImg.getWidth();
		// 灰度化，灰度值=0.3R+0.59G+0.11B：
		BufferedImage grayImage = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		for (int y = minY; y < height; y++) {
			for (int x = minX; x < width; x++) {
				int rgb = srcImg.getRGB(x, y);
				Color color = new Color(rgb); // 根据rgb的int值分别取得r,g,b颜色。
				int gray = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());
				Color newColor = new Color(gray, gray, gray);
				grayImage.setRGB(x, y, newColor.getRGB());
			}
		}
		ImageIO.write(grayImage, "jpg", new File(imageFile.getParent(), "1.jpg"));
		BufferedImage buffImg = grayImage;
		// 灰度反转
		for (int y = minY; y < height; y++) {
			for (int x = minX; x < width; x++) {
				int rgb = buffImg.getRGB(x, y);
				Color color = new Color(rgb); // 根据rgb的int值分别取得r,g,b颜色。
				Color newColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
				buffImg.setRGB(x, y, newColor.getRGB());
			}
		}
		ImageIO.write(buffImg, "jpg", new File(imageFile.getParent(), "2.jpg"));
		int average = getAverage(buffImg);
		BufferedImage binaryImage = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		// 再次是二值化，取图片的平均灰度作为阈值，低于该值的全都为0，高于该值的全都为255：
		for (int y = minY; y < height; y++) {
			for (int x = minX; x < width; x++) {
				int rgb = buffImg.getRGB(x, y);
				Color color = new Color(rgb); // 根据rgb的int值分别取得r,g,b颜色。
				int value = 255 - color.getBlue();
				if (value > average) {
					Color newColor = new Color(0, 0, 0);
					binaryImage.setRGB(x, y, newColor.getRGB());
				} else {
					Color newColor = new Color(255, 255, 255);
					binaryImage.setRGB(x, y, newColor.getRGB());
				}
			}
		}
		ImageIO.write(binaryImage, "jpg", new File(imageFile.getParent(), "3.jpg"));
		imageFile = new File(imageFile.getParent(), "ocr.tif");
		ImageIO.write(binaryImage, "tif", imageFile);
		String tessPath = "D:/lezo/iscript/v0.01/iscript-core/iscript-core-ocr/src/main/resources/tesseract";
		ProcessBuilder pBuilder = new ProcessBuilder();
		File outputFile = setProcessBuilder(pBuilder, imageFile, tessPath);
		int w = pBuilder.start().waitFor();
		System.out.println("w:" + w);

		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsoluteFile()), "UTF-8"));
			while (br.ready()) {
				sb.append(br.readLine() + "\n");
			}
			System.out.println("result:" + sb.toString());
		} finally {
			if (br != null) {
				br.close();
			}
			imageFile.deleteOnExit();
			outputFile.deleteOnExit();
		}
	}

	private static File setProcessBuilder(ProcessBuilder pb, File imageFile, String tessPath) {
		String fileName = "ocrout";
		File outputFile = new File(imageFile.getParentFile(), fileName + ".txt");
		if (!outputFile.exists()) {
			outputFile.getParentFile().mkdirs();
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<String> cmd = new ArrayList<String>();
		cmd.add(tessPath + File.separator + "tesseract");
		cmd.add("");
		cmd.add(fileName);
		cmd.add(LANG_OPTION);
		cmd.add("eng");
		cmd.add("-psm 7");
		pb.directory(imageFile.getParentFile());

		cmd.set(1, imageFile.getName());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		return outputFile;
	}

	private int getAverage(BufferedImage buffImg) {
		int total = buffImg.getWidth() * buffImg.getHeight();
		int sum = 0;
		for (int h = 0; h < buffImg.getHeight(); h++) {
			for (int w = 0; w < buffImg.getWidth(); w++) {
				int rgb = buffImg.getRGB(w, h);
				sum += new Color(rgb).getBlue();
			}
		}
		return sum / total;
	}
}
