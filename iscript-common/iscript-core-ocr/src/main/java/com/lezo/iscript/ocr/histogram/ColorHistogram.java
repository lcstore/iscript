package com.lezo.iscript.ocr.histogram;

import java.awt.image.Raster;

public class ColorHistogram {
	private static final int GRAM_SIZE = 1 << 8;

	public static int[] toHistogram(Raster raster, ColorDecider decider) {
		int width = raster.getWidth();
		int height = raster.getHeight();
		int[] hisArr = new int[GRAM_SIZE];
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int color = decider.getColor(w, h);
				hisArr[color]++;
			}
		}
		return hisArr;

	}

}
