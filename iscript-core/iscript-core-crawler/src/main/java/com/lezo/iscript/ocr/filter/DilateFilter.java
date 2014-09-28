package com.lezo.iscript.ocr.filter;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * 膨胀是将与物体接触的所有背景点合并到该物体中，使边界向外部扩张的过程。
 * http://blog.csdn.net/bagboy_taobao_com/article/details/5574159
 * 
 * @author lilinchong
 * 
 */
public class DilateFilter implements IFilterable {
	private static final int CIRCLE_RADIUS = 3;

	@Override
	public BufferedImage doFilter(BufferedImage source) {
		if (source.getType() != BufferedImage.TYPE_BYTE_BINARY) {
			source = new BinaryFilter().doFilter(source);
		}
		int fgClr = getForegroundClr(source);
		for (int y = 1; y < source.getHeight(); y++) {
			for (int x = 1; x < source.getWidth(); x++) {
				doDilate(fgClr, x, y, source);
			}
		}
		return source;
	}

	private int getForegroundClr(BufferedImage image) {
		int[] clrArr = new int[2];
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int rgb = image.getRGB(x, y);
				if (rgb == Color.BLACK.getRGB()) {
					clrArr[0]++;
				} else {
					clrArr[1]++;
				}
			}
		}
		int fgClr = (clrArr[0] < clrArr[1]) ? Color.BLACK.getRGB() : Color.WHITE.getRGB();
		return fgClr;
	}

	private void doDilate(int fgClr, int x, int y, BufferedImage source) {
		boolean bBothFill = true;

		for (int h = -CIRCLE_RADIUS; h <= CIRCLE_RADIUS; h++) {
			int yh = y + h;
			if (yh < 0 || yh >= source.getHeight()) {
				bBothFill = false;
				break;
			}
			for (int w = -CIRCLE_RADIUS; w <= CIRCLE_RADIUS; w++) {
				int xw = x + w;
				if (xw < 0 || yh >= source.getWidth()) {
					bBothFill = false;
					break;
				}
				int rgb = source.getRGB(x, y);
				bBothFill = (fgClr == rgb);
				if (bBothFill) {
					break;
				}
			}
			if (bBothFill) {
				break;
			}
		}
		if (!bBothFill) {
			source.setRGB(x, y, (fgClr == Color.BLACK.getRGB()) ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
		} else {
			source.setRGB(x, y, fgClr);
		}

	}

}
