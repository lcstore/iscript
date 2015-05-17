package com.lezo.iscript.image.getable;

import java.awt.image.BufferedImage;

public interface RasterGetable {
	int getValue(BufferedImage image, int x, int y);
}
