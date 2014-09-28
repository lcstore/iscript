package com.lezp.iscript;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

public class WitheFilterTest {

	@Test
	public void testWihte() throws Exception {
		File imageFile = new File("src/main/resources/", "4tv2.jpg");
		BufferedImage wImage = removeBackgroud(imageFile.getAbsolutePath());
		ImageIO.write(wImage, "jpg", new File(imageFile.getParent(), "wihte2.jpg"));
	}

	public static int isWhite(int colorInt) {
		Color color = new Color(colorInt);
		if (color.getGreen() > 180 && color.getGreen() < 190) {
			return 1;
		}
		return 0;
	}

	public static BufferedImage removeBackgroud(String picFile) throws Exception {
		BufferedImage img = ImageIO.read(new File(picFile));
		int width = img.getWidth();
		int height = img.getHeight();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (isWhite(img.getRGB(x, y)) != 1) {
					img.setRGB(x, y, Color.WHITE.getRGB());
				} else {
					img.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}
		return img;
	}
}
