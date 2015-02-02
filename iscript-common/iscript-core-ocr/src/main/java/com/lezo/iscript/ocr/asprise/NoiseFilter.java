package com.lezo.iscript.ocr.asprise;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NoiseFilter {

	public static BufferedImage toAvg(BufferedImage image) {
		BufferedImage img = image;
		BufferedImage outBinary = img;
		int width = img.getWidth();
		int height = img.getHeight();
		int area = width * height;
		int gray[][] = new int[width][height];
		int u = 0;// 灰度平均值
		int graybinary;
		int graysum = 0;
		int graymean = 0;
		int grayfrontmean = 0;
		int graybackmean = 0;
		Color color;
		int pixl[][] = new int[width][height];
		int pixelsR;
		int pixelsG;
		int pixelsB;
		int pixelGray;
		int T = 0;
		int front = 0;
		int back = 0;
		for (int i = 1; i < width; i++) { // 不算边界行和列，为避免越界
			for (int j = 1; j < height; j++) {
				pixl[i][j] = img.getRGB(i, j);
				color = new Color(pixl[i][j]);
				pixelsR = color.getRed();// R空间
				pixelsB = color.getBlue();// G空间
				pixelsG = color.getGreen();// B空间
				pixelGray = (int) (0.3 * pixelsR + 0.59 * pixelsG + 0.11 * pixelsB);// 计算每个坐标点的灰度
				gray[i][j] = (pixelGray << 16) + (pixelGray << 8) + (pixelGray);
				graysum += pixelGray;
			}
		}
		graymean = (int) (graysum / area);// 整个图的灰度平均值
		u = graymean;
		System.out.println(u);
		for (int i = 0; i < width; i++) // 计算整个图的二值化阈值
		{
			for (int j = 0; j < height; j++) {
				if (((gray[i][j]) & (0x0000ff)) < graymean) {
					graybackmean += ((gray[i][j]) & (0x0000ff));
					back++;
				} else {
					grayfrontmean += ((gray[i][j]) & (0x0000ff));
					front++;
				}
			}
		}
		int frontvalue = (int) (grayfrontmean / front);// 前景中心
		int backvalue = (int) (graybackmean / back);// 背景中心
		float G[] = new float[frontvalue - backvalue + 1];// 方差数组
		int s = 0;
		System.out.println(front);
		System.out.println(frontvalue);
		System.out.println(backvalue);
		for (int i1 = backvalue; i1 < frontvalue + 1; i1++)// 以前景中心和背景中心为区间采用大津法算法
		{
			back = 0;
			front = 0;
			grayfrontmean = 0;
			graybackmean = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (((gray[i][j]) & (0x0000ff)) < (i1 + 1)) {
						graybackmean += ((gray[i][j]) & (0x0000ff));
						back++;
					} else {
						grayfrontmean += ((gray[i][j]) & (0x0000ff));
						front++;
					}
				}
			}
			grayfrontmean = (int) (grayfrontmean / front);
			graybackmean = (int) (graybackmean / back);
			G[s] = (((float) back / area) * (graybackmean - u) * (graybackmean - u) + ((float) front / area)
					* (grayfrontmean - u) * (grayfrontmean - u));
			s++;
		}
		float max = G[0];
		int index = 0;
		for (int i = 1; i < frontvalue - backvalue + 1; i++) {
			if (max < G[i]) {
				max = G[i];
				index = i;
			}
		}
		// System.out.println(G[index]);
		// System.out.println(index);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (((gray[i][j]) & (0x0000ff)) < (index + backvalue)) {
					outBinary.setRGB(i, j, 0x000000);
				} else {
					outBinary.setRGB(i, j, 0xffffff);
				}
			}
		}
		return outBinary;
	}

	public static BufferedImage doFilter(BufferedImage image) {
		Map<Integer, Integer> mapColor = new HashMap<Integer, Integer>();
		int width = image.getWidth();
		int height = image.getHeight();
		Raster raster = image.getData();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// Object outData = raster.getDataElements(i, j, null);
				// int color = (image.getType() == BufferedImage.TYPE_BYTE_GRAY)
				// ? image.getColorModel().getRGB(outData)
				// : image.getColorModel().getRed(outData);
				// int color = image.getColorModel().getRed(outData);
				int color = image.getRGB(i, j);
				Integer count = mapColor.get(color);
				if (count == null) {
					count = 0;
				}
				count++;
				mapColor.put(color, count);
			}
		}
		List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(mapColor.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});
		int size = list.size();
		int max = (size < 5) ? size : 5;
		list = list.subList(0, max);
		int intBack = list.get(0).getKey();
		Set<Integer> setColor = new HashSet<Integer>();
		for (Map.Entry<Integer, Integer> entry : list) {
			setColor.add(entry.getKey());
		}
		// Raster raster = image.getData();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int color = image.getRGB(i, j);
				if (setColor.contains(color)) {
					continue;
				}
				image.setRGB(i, j, intBack);
			}
		}
		return image;
	}
}
