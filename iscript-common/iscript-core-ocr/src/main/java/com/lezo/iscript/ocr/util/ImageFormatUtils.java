package com.lezo.iscript.ocr.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageFormatUtils {
	public static BufferedImage format(BufferedImage image, String format) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, format, out);
		InputStream input = new ByteArrayInputStream(out.toByteArray());
		return ImageIO.read(input);
	}
}
