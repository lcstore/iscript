package com.lezo.iscript.image.handle;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class SimpleGrayHandler implements ImageHandle {

	@Override
	public BufferedImage doHandle(BufferedImage image) {
		if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
			return image;
		}
		BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		for (int h = 0; h < image.getHeight(); h++) {
			for (int w = 0; w < image.getWidth(); w++) {
				int pixel = image.getRGB(w, h);
				grayImage.setRGB(w, h, pixel);
			}
		}
		return grayImage;
	}

	public static int toGrayPixel(int gray) {
		int rgb4Gray = (gray << 16 | gray << 8 | gray);
		return rgb4Gray;
	}

	public int toGray(int pixel, ColorModel cm) {
		int r = cm.getRed(pixel);
		int g = cm.getGreen(pixel);
		int b = cm.getBlue(pixel);
		int gray = (int) (r * 0.30 + g * 0.59 + b * 0.11);
		// int gray = (r * 38 + g * 75 + b * 15) >> 7;
		return gray;
	}

}
