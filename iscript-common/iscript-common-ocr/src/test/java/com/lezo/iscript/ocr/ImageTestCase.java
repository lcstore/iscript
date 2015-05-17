package com.lezo.iscript.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;

public class ImageTestCase {
	private BufferedImage image;

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	@Before
	public void setUp() {
		File imageFile = new File("R42L.jpg");
		imageFile = new File("4tv2.jpg");
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			System.out.println("Fail to read " + imageFile);
			e.printStackTrace();
		}
	}
}
