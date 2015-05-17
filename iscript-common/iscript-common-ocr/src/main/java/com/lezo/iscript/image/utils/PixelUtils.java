package com.lezo.iscript.image.utils;


public class PixelUtils {



	/**
	 * 灰度值转换为灰度像素，无alpha通道
	 * 
	 * @param gray
	 * @return
	 */
	public static int toGrayPixel(int gray) {
		int rgb4Gray = (gray << 16 | gray << 8 | gray);
		return rgb4Gray;
	}

	/**
	 * 转换到真实的灰度值 效果较好(2)
	 * 
	 * @param pixel
	 * @return
	 */
	public static int toGray(int pixel) {
		int r = getRed(pixel);
		int g = getGreen(pixel);
		int b = getBlue(pixel);
		int rgbGray = (r * 38 + g * 75 + b * 15) >> 7;
		return rgbGray;
	}

	/**
	 * Adobe RGB (1998) [gamma=2.20] Gray = (R^2.2 * 0.2973 + G^2.2 * 0.6274 +
	 * B^2.2 * 0.0753)^(1/2.2)
	 * 
	 * 速度稍慢，但效果好(1)
	 * 
	 * @param pixel
	 * @return
	 */
	public static int toGrayByAdobe(int pixel) {
		int r = getRed(pixel);
		int g = getGreen(pixel);
		int b = getBlue(pixel);

		double underPow = (Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2) * 0.6274 + Math.pow(b, 2.2) * 0.0753);
		int rgbGray = (int) Math.pow(underPow, 1 / 2.2);
		return rgbGray;
	}

	/**
	 * 取RGB各分量的平均值，作为灰度值
	 * 
	 * 效果比较差(3)
	 * 
	 * @param pixel
	 * @return
	 */
	public static int toGrayByMean(int pixel) {
		int r = getRed(pixel);
		int g = getGreen(pixel);
		int b = getBlue(pixel);

		int rgbGray = (r + g + b) / 3;
		return rgbGray;
	}

	public static int clamp(int c) {
		if (c < 0)
			return 0;
		if (c > 255)
			return 255;
		return c;
	}

	public static int interpolate(int v1, int v2, float f) {
		return clamp((int) (v1 + f * (v2 - v1)));
	}

	public static int brightness(int rgb) {
		int r = rgb >> 16 & 0xFF;
		int g = rgb >> 8 & 0xFF;
		int b = rgb & 0xFF;
		return (r + g + b) / 3;
	}

	public static int getRed(int pixel) {
		int red = (pixel >> 16) & 0xff;
		return red;
	}

	public static int getGreen(int pixel) {
		int green = (pixel >> 8) & 0xff;
		return green;
	}

	public static int getBlue(int pixel) {
		int blue = (pixel) & 0xff;
		return blue;
	}

	public static int getAlpha(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		return alpha;
	}

	/**
	 * 各分量都是dn的RGB值
	 * 
	 * @param dn
	 * @return
	 */
	public static int getPixel(int red, int green, int blue, int alpha) {
		return (alpha << 24 | red << 16 | green << 8 | blue);
	}

	public static boolean nearColors(int rgb1, int rgb2, int tolerance) {
		int r1 = rgb1 >> 16 & 0xFF;
		int g1 = rgb1 >> 8 & 0xFF;
		int b1 = rgb1 & 0xFF;
		int r2 = rgb2 >> 16 & 0xFF;
		int g2 = rgb2 >> 8 & 0xFF;
		int b2 = rgb2 & 0xFF;
		return (Math.abs(r1 - r2) <= tolerance) && (Math.abs(g1 - g2) <= tolerance) && (Math.abs(b1 - b2) <= tolerance);
	}

}