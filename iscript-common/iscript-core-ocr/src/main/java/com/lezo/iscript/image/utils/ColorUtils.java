package com.lezo.iscript.image.utils;

import java.awt.image.ColorModel;

public class ColorUtils {

	public static int getGrayPixel(int gray) {
		int rgb4Gray = (gray << 16 | gray << 8 | gray);
		return rgb4Gray;
	}

	public static int getGrayBand(int pixel, ColorModel cm) {
		int r = cm.getRed(pixel);
		int g = cm.getGreen(pixel);
		int b = cm.getBlue(pixel);
		int gray = (r * 38 + g * 75 + b * 15) >> 7;
		return gray;
	}
}
