package com.lezo.iscript.image.handle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lezo.iscript.image.getable.RasterGetable;
import com.lezo.iscript.image.utils.LumSortUtils;

public class BinaryHandler implements ImageHandle {

	private RasterGetable getable;

	public BinaryHandler(RasterGetable getable) {
		super();
		this.getable = getable;
	}

	@Override
	public BufferedImage doHandle(BufferedImage image) {
		BufferedImage binaryImage = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_BYTE_BINARY);
		for (int h = 0; h < image.getHeight(); h++) {
			for (int w = 0; w < image.getWidth(); w++) {
				int pixel = image.getRGB(w, h);
				binaryImage.setRGB(w, h, pixel);
			}
		}
		return binaryImage;
	}

	public int getSpliter(BufferedImage image) {
		Map<Integer, Integer> lumMap = LumSortUtils.doAssort(image, getable);
		if (lumMap.size() < 2) {
			return 256;
		}
		List<Entry<Integer, Integer>> entryList = new ArrayList<Entry<Integer, Integer>>(lumMap.entrySet());
		Collections.sort(entryList, new Comparator<Entry<Integer, Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		int firstCrest = entryList.get(0).getKey();
		int secondCrest = entryList.get(1).getKey();
		int firstTrough = (firstCrest + secondCrest) / 2;
		int min = firstCrest;
		int max = min;
		if (firstCrest < secondCrest) {
			max = secondCrest;
		} else {
			min = secondCrest;
		}
		int minSum = entryList.get(0).getValue();
		for (Entry<Integer, Integer> entry : entryList) {
			if (entry.getKey() > min && entry.getKey() < max && minSum > entry.getValue()) {
				minSum = entry.getValue();
				firstTrough = entry.getKey();
			}
		}
		return firstTrough;
	}

	public int getAverageColor(BufferedImage image, int x, int y) {
		int rs = image.getRGB(x, y);
		rs += x == 0 ? 255 : image.getRGB(x - 1, y);
		rs += (x == 0 || y == 0 ? 255 : image.getRGB(x - 1, y - 1));
		rs += (x == 0 || y == image.getHeight() - 1 ? 255 : image.getRGB(x - 1, y + 1));
		rs += (y == 0 ? 255 : image.getRGB(x, y - 1));
		rs += (y == image.getHeight() - 1 ? 255 : image.getRGB(x, y + 1));
		rs += (x == image.getWidth() - 1 ? 255 : image.getRGB(x + 1, y));
		rs += (x == image.getWidth() - 1 || y == 0 ? 255 : image.getRGB(x + 1, y - 1));
		rs += (x == image.getWidth() - 1 || y == image.getHeight() - 1 ? 255 : image.getRGB(x + 1, y + 1));
		return rs / 9;
	}
}
