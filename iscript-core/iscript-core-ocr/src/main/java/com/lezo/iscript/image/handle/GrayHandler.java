package com.lezo.iscript.image.handle;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import com.lezo.iscript.image.utils.ColorUtils;

public class GrayHandler implements ImageHandle {

	@Override
	public BufferedImage doHandle(BufferedImage image) {
		if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
			return image;
		}
		BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		for (int h = 0; h < image.getHeight(); h++) {
			for (int w = 0; w < image.getWidth(); w++) {
				int pixel = image.getRGB(w, h);
				int gray = ColorUtils.getGrayBand(pixel, ColorModel.getRGBdefault());
				grayImage.setRGB(w, h, ColorUtils.getGrayPixel(gray));
			}
		}
		return grayImage;
	}

}
