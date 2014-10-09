package com.lezo.iscript.ocr.filter;

import java.awt.image.BufferedImage;

public class GrayFilter implements IFilterable {

	@Override
	public BufferedImage doFilter(BufferedImage source) {
		if (source.getType() == BufferedImage.TYPE_BYTE_GRAY) {
			return source;
		}
		BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				int rgb = source.getRGB(x, y);
				image.setRGB(x, y, rgb);
			}
		}
		return image;
	}

}
