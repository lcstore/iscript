package com.lezo.iscript.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.junit.Test;

public class FrontTest {

	@Test
	public void testCreate() throws Exception {
		// Font f = Font.createFont(Font.TRUETYPE_FONT, new
		// FileInputStream("src/test/resources/front/SIMLI.TTF"));
		// Font f = Font.createFont(Font.TRUETYPE_FONT, new
		// FileInputStream("src/test/resources/front/simsun.ttc"));
		// String name = f.getName();
		// System.out.println(name);
		Font f = new Font("宋体", Font.PLAIN, 16);
		int width = 124;
		int height = 124;
		int type = BufferedImage.TYPE_3BYTE_BGR;
		String source = "李林冲";
		BufferedImage bimg = new BufferedImage(width, height, type);
		Graphics g = bimg.getGraphics();
		Color oldClr = g.getColor();
		g.setColor(Color.RED);
		g.setFont(f);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.green);
		g.clearRect(50, 50, 100, 100);
		g.drawString(source, 50, 50);
		String format = "jpg";
		File output = new File("src/test/resources/front/testImage" + "." + format);
		g.dispose();
		ImageIO.write(bimg, format, output);
	}
}
