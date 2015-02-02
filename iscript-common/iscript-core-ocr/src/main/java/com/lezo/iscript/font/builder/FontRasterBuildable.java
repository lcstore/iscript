package com.lezo.iscript.font.builder;

import java.awt.image.BufferedImage;

import com.lezo.iscript.font.FontRaster;

public interface FontRasterBuildable {
	FontRaster buildFontRaster(BufferedImage image);
}
