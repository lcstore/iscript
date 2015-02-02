package com.lezo.iscript.font.builder;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lezo.iscript.font.FontDot;
import com.lezo.iscript.font.FontRaster;
import com.lezo.iscript.font.analyzer.FontRasterAnalyzable;
import com.lezo.iscript.font.analyzer.FontRasterAnalyzer;
import com.lezo.iscript.image.getable.RasterGetable;
import com.lezo.iscript.image.getable.SplitGetter;
import com.lezo.iscript.image.handle.GrayHandler;
import com.lezo.iscript.image.utils.ImageUtils;
import com.lezo.iscript.image.utils.LumSortUtils;

public class FontRasterBuilder implements FontRasterBuildable {

	@Override
	public FontRaster buildFontRaster(BufferedImage image) {
		image = ImageUtils.doHandle(image, new GrayHandler());
		SplitGetter getter = getSpliter(image);
		List<FontDot> dotList = new ArrayList<FontDot>();
		for (int h = 0; h < image.getHeight(); h++) {
			for (int w = 0; w < image.getWidth(); w++) {
				int r = getter.getValue(image, w, h);
				if (r > 0) {
					FontDot dot = new FontDot(new Point(w, h));
					dotList.add(dot);
				}
			}
		}
		FontRasterAnalyzable analyzer = new FontRasterAnalyzer();
		FontRaster fontRaster = new FontRaster(dotList);
		analyzer.doAnalyze(fontRaster);
		return fontRaster;
	}

	public SplitGetter getSpliter(BufferedImage image) {
		final ColorModel cm = ColorModel.getRGBdefault();
		Map<Integer, Integer> lumMap = LumSortUtils.doAssort(image, new RasterGetable() {
			@Override
			public int getValue(BufferedImage image, int x, int y) {
				int data = image.getRGB(x, y);
				return cm.getRed(data);
			}
		});
		List<Entry<Integer, Integer>> entryList = new ArrayList<Entry<Integer, Integer>>(lumMap.entrySet());
		Collections.sort(entryList, new Comparator<Entry<Integer, Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		if (entryList.size() < 2) {
			int split = entryList.get(0).getKey() / 2;
			int left = 0;
			int right = 0;
			return new SplitGetter(split, left, right);
		}
		int firstClr = entryList.get(0).getKey();
		int secondClr = entryList.get(1).getKey();
		int split = (firstClr + secondClr) / 2;
		Integer lowValue = null;
		for (Entry<Integer, Integer> entry : entryList) {
			if (entry.getKey() <= secondClr || entry.getKey() >= firstClr) {
				continue;
			}
			if (lowValue == null) {
				split = entry.getKey();
				lowValue = entry.getValue();
			} else if (lowValue > entry.getValue()) {
				split = entry.getKey();
				lowValue = entry.getValue();
			}
		}
		int left = (secondClr <= 10) ? 1 : 0;
		int right = (firstClr >= 250) ? 0 : 1;
		return new SplitGetter(split, left, right);
	}
}
