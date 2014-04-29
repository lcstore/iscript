package com.lezo.iscript.image.utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.lezo.iscript.image.handle.ImageHandle;

public class ImageUtils {

	public static BufferedImage doHandle(BufferedImage image, ImageHandle handler) {
		return handler.doHandle(image);
	}

	public static BufferedImage createImage(String source, int width, int height, Font font) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		Graphics gc = bi.getGraphics();
		gc.setFont(font);
		FontMetrics fm = gc.getFontMetrics();
		int stringWidth = fm.stringWidth(source);
		int stringAscent = fm.getAscent();
		int stringDescent = fm.getDescent();
		int sSub = stringAscent - stringDescent;
		int x = bi.getWidth() / 2 - stringWidth / 2;
		int y = bi.getHeight() / 2 + sSub / 2 - (sSub % 2 > 0 ? 1 : 0);
		gc.drawString(source, x, y);
		gc.dispose();
		return bi;
	}
}
