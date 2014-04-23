package com.lezo.iscript.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.lezo.iscript.image.getable.RasterGetable;
import com.lezo.iscript.image.handle.BinaryHandler;
import com.lezo.iscript.image.handle.ImageHandle;
import com.lezo.iscript.image.utils.ImageUtils;

public class BinaryHandlerTest {

	@Test
	public void handle() throws IOException {
		File imgFile = new File("src/test/resources/img/ancc" + "/42R6.gif");
		BufferedImage image = ImageIO.read(imgFile);
		RasterGetable getable = new RasterGetable() {
			@Override
			public int getValue(BufferedImage image, int x, int y) {
				Raster raster = image.getData();
				DataBuffer dBuf = raster.getDataBuffer();
				int index = y * raster.getWidth() + x;
				int data = dBuf.getElem(index);
				data = image.getRGB(x, y);
				return data;
			}
		};
		ImageHandle handler = new BinaryHandler(getable);
		BufferedImage result = ImageUtils.doHandle(image, handler);
		ImageIO.write(result, "jpg", new File("src/test/resources/img/ancc" + "/42R6-binary.jpg"));
	}
}
