package com.lezo.iscript.image.utils;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.HashMap;
import java.util.Map;

import com.lezo.iscript.image.getable.RasterGetable;

/**
 * luminosity亮度.亮度分类，即灰度直方图统计
 * 
 * @author lilinchong
 * 
 */
public class LumSortUtils {

	public static Map<Integer, Integer> doAssort(BufferedImage image, RasterGetable getable) {
		Map<Integer, Integer> lumMap = new HashMap<Integer, Integer>();
		Raster raster = image.getData();
		for (int h = 0; h < raster.getHeight(); h++) {
			for (int w = 0; w < raster.getWidth(); w++) {
				int lum = getable.getValue(image, w, h);
				Integer lumSum = lumMap.get(lum);
				if (lumSum == null) {
					lumSum = 0;
				}
				lumMap.put(lum, lumSum + 1);
			}
		}
		return lumMap;
	}
}
