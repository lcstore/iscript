package com.lezo.iscript.image;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.lezo.iscript.image.getable.RasterGetable;
import com.lezo.iscript.image.handle.GrayHandler;
import com.lezo.iscript.image.utils.ImageUtils;
import com.lezo.iscript.image.utils.LumSortUtils;

public class LumSortUtilsTest {
	@Test
	public void doAssort() throws Exception {
		File imgFile = new File("src/test/resources/img/ancc" + "/42R6-gray.jpg");
		// File imgFile = new File("src/test/resources/img/ancc" + "/42R6.gif");
		BufferedImage image = ImageIO.read(imgFile);
		System.out.println(image.isAlphaPremultiplied());
		final BufferedImage destImage = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_BYTE_BINARY);
		Map<Integer, Integer> lumMap = LumSortUtils.doAssort(image, new RasterGetable() {
			@Override
			public int getValue(BufferedImage image, int x, int y) {
				Raster raster = image.getData();
				DataBuffer dBuf = raster.getDataBuffer();
				int index = y * raster.getWidth() + x;
				int data = image.getRGB(x, y);
				destImage.setRGB(x, y, data);
				return data;
			}
		});
		List<Entry<Integer, Integer>> entryList = new ArrayList<Entry<Integer, Integer>>(lumMap.entrySet());
		Collections.sort(entryList, new Comparator<Entry<Integer, Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		for (Entry<Integer, Integer> entry : entryList) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		ImageIO.write(destImage, "jpg", new File("src/test/resources/img/ancc" + "/42R6-gray123.jpg"));
	}

	@Test
	public void testLum() throws IOException {
		String imgPath = "src/test/resources/img/" + "pure/st16.png";
		BufferedImage image = ImageIO.read(new File(imgPath));
		BufferedImage lumImg = ImageUtils.doHandle(image, new GrayHandler());
		final ColorModel cm = ColorModel.getRGBdefault();
		Map<Integer, Integer> lumMap = LumSortUtils.doAssort(lumImg, new RasterGetable() {
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
		for (Entry<Integer, Integer> entry : entryList) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}
}
