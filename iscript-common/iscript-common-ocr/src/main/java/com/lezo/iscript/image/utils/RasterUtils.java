package com.lezo.iscript.image.utils;

import java.awt.image.BufferedImage;

public class RasterUtils {

	public static int[] getRasterData(BufferedImage image, int x, int y, int width, int height) {
		int type = image.getType();
		if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
			return (int[]) image.getRaster().getDataElements(x, y, width, height, null);
		else {
			return image.getRGB(x, y, width, height, null, 0, width);
		}
	}

	public static void setRasterData(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
		int type = image.getType();
		if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
			image.getRaster().setDataElements(x, y, width, height, pixels);
		} else {
			image.setRGB(x, y, width, height, pixels, 0, width);
		}
	}
}
