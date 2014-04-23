package com.lezo.iscript.ocr.tesseract;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

public class TesseractTest {

	@Test
	public void test43R6() throws IOException {
		String expected = "42R6";
		String actual = "";
		File imageFile = new File("src/test/resources/img/ancc/" + "42R6.gif");
		BufferedImage image = ImageIO.read(imageFile);
		int iw = image.getWidth();
		int ih = image.getHeight();
		int[] pixels = new int[iw * ih];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih, pixels, 0, iw);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ColorModel cm = ColorModel.getRGBdefault();
		for (int y = 0; y < ih; y++) {
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x < iw; x++) {
				int pixel = pixels[y * iw + x];
				// int alpha = cm.getAlpha(pixel);
				int alpha = cm.getBlue(pixel);
				alpha = (alpha < 100) ? 0 : 1;
				sb.append(alpha + " ");
			}
			System.out.println(sb.toString());
		}
		// Assert.assertEquals(expected, actual);
	}
}
