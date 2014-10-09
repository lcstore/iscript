package com.lezo.iscript.font;

import java.awt.Font;
import java.awt.image.BufferedImage;

import org.junit.Test;

import com.lezo.iscript.font.builder.FontRasterBuilder;
import com.lezo.iscript.image.utils.ImageUtils;
import com.lezo.iscript.similar.FontRasterSimilar;

public class FontRasterSimilarTest {

	@Test
	public void testRasterSimilar() {
		FontRasterBuilder builder = new FontRasterBuilder();
		String source = "8";
		Font font = new Font("宋体", Font.PLAIN, 16);
		int width = font.getSize();
		int height = width;
		BufferedImage image = ImageUtils.createImage(source, width, height, font);
		FontRaster fontRaster = builder.buildFontRaster(image);

		source = "0";
		font = new Font("宋体", Font.PLAIN, 16);
		width = font.getSize();
		height = width;
		image = ImageUtils.createImage(source, width, height, font);
		FontRaster oFontRaster = builder.buildFontRaster(image);
		FontRasterSimilar similar = new FontRasterSimilar(fontRaster, oFontRaster);
		System.out.println(similar.getSimilarity());
	}

}
