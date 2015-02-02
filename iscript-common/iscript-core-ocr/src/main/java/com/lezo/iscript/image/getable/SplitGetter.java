package com.lezo.iscript.image.getable;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class SplitGetter implements RasterGetable {
	private int split;
	private int leftValue;
	private int rightValue;

	public SplitGetter(int split, int leftValue, int rightValue) {
		super();
		this.split = split;
		this.leftValue = leftValue;
		this.rightValue = rightValue;
	}

	@Override
	public int getValue(BufferedImage image, int x, int y) {
		int pixel = image.getRGB(x, y);
		int data = ColorModel.getRGBdefault().getRed(pixel);
		return (data > split) ? rightValue : leftValue;
	}

}
