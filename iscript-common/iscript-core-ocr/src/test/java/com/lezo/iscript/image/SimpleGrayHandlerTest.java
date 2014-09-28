package com.lezo.iscript.image;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.lezo.iscript.image.handle.SimpleGrayHandler;
import com.lezo.iscript.image.utils.ImageUtils;

public class SimpleGrayHandlerTest {

	@Test
	public void handle() throws Exception{
		File imgFile = new File("src/test/resources/img/ancc" + "/42R6.gif");
		BufferedImage image = ImageIO.read(imgFile);
		BufferedImage result = ImageUtils.doHandle(image, new SimpleGrayHandler());
		ImageIO.write(result, "jpg", new File("src/test/resources/img/ancc" + "/42R6-simplegray.jpg"));
	}
}
