package com.lezo.iscript.ocr.asprise;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class GrayFilter {

	public static BufferedImage toBinaryImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = image.getRGB(i, j);
				grayImage.setRGB(i, j, rgb);
			}
		}
		return grayImage;
	}

	public static BufferedImage doFilter(BufferedImage image) {
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		BufferedImage grayPicture = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
		ColorConvertOp cco = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		cco.filter(image, grayPicture);
		return grayPicture;
	}

	public static BufferedImage getGrayPicture(BufferedImage originalImage) {
		int green = 0, red = 0, blue = 0, rgb;
		int imageWidth = originalImage.getWidth();
		int imageHeight = originalImage.getHeight();
		BufferedImage routeImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);
		for (int i = originalImage.getMinX(); i < imageWidth; i++) {
			for (int j = originalImage.getMinY(); j < imageHeight; j++) {
				// 图片的像素点其实是个矩阵，这里利用两个for循环来对每个像素进行操作
				Object data = originalImage.getRaster().getDataElements(i, j, null);// 获取该点像素，并以object类型表示
				red = originalImage.getColorModel().getRed(data);
				blue = originalImage.getColorModel().getBlue(data);
				green = originalImage.getColorModel().getGreen(data);
				red = (red * 3 + green * 6 + blue * 1) / 10;
				green = red;
				blue = green;
				/*
				 * 这里将r、g、b再转化为rgb值，因为bufferedImage没有提供设置单个颜色的方法，只能设置rgb。
				 * rgb最大为8388608，当大于这个值时，应减去255*255*255即16777216
				 */
				rgb = (red * 256 + green) * 256 + blue;
				if (rgb > 8388608) {
					rgb = rgb - 16777216;
				}
				// 将rgb值写回图片
				routeImage.setRGB(i, j, rgb);
			}

		}

		return routeImage;
	}
}
