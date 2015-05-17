package com.lezo.iscript.ocr.histogram;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.lezo.iscript.ocr.ImageTestCase;

public class ColorHistogramTest extends ImageTestCase {

	@Test
	public void doHistogram() {
		BufferedImage image = getImage();
		final ColorModel model = image.getColorModel();
		final Raster raster = image.getRaster();
		int[] his = ColorHistogram.toHistogram(raster, new ColorDecider() {
			public int getColor(int w, int h) {
				Object outData = raster.getDataElements(w, h, null);
				int color = model.getGreen(outData);
				return color;
			}
		});
		int[] doAssortHis = HistogramAssort.doAssort(his, new ColorAssorter() {
			public int doAssort(int color) {
				return color;
			}
		});
		Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < doAssortHis.length; i++) {
			colorMap.put(i, doAssortHis[i]);
		}

		List<Entry<Integer, Integer>> entryList = new ArrayList<Entry<Integer, Integer>>(colorMap.entrySet());
		Collections.sort(entryList, new Comparator<Entry<Integer, Integer>>() {
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});
		for (Entry<Integer, Integer> entry : entryList) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}

	@Test
	public void doSimpleHistogram() {
		BufferedImage image = getImage();
		final ColorModel model = image.getColorModel();
		final Raster raster = image.getRaster();
		int[] his = ColorHistogram.toHistogram(raster, new ColorDecider() {
			public int getColor(int w, int h) {
				Object outData = raster.getDataElements(w, h, null);
				int color = model.getGreen(outData);
				return color;
			}
		});
		Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < his.length; i++) {
			colorMap.put(i, his[i]);
		}

		List<Entry<Integer, Integer>> entryList = new ArrayList<Entry<Integer, Integer>>(colorMap.entrySet());
		Collections.sort(entryList, new Comparator<Entry<Integer, Integer>>() {
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o1.getKey()- o2.getKey();
			}
		});
		for (Entry<Integer, Integer> entry : entryList) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}
}
