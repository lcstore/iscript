package com.lezo.iscript.image;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

public class BlandAnalyze {

	@Test
	public void bandAnalyze() throws Exception {
		String imgPath = "src/test/resources/img/" + "ancc/42R6.gif";
		imgPath = "src/test/resources/img/" + "pure/st16.png";
		File imgFile = new File(imgPath);
		BufferedImage bufImg = ImageIO.read(imgFile);
		Raster raster = bufImg.getData();
		DataBuffer dBuf = raster.getDataBuffer();
		ColorModel clrModel = ColorModel.getRGBdefault();
		BufferedImage dImg = new BufferedImage(raster.getWidth(), raster.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		int[] lumArray = new int[256];
		for (int h = 0; h < raster.getHeight(); h++) {
			for (int w = 0; w < raster.getWidth(); w++) {
				int index = h * raster.getWidth() + w;
				int data = dBuf.getElem(index);
				int rData = clrModel.getRed(data) + clrModel.getBlue(data) + clrModel.getGreen(data);
				rData /= 3;
				// dBuf.setElem(index, (clrModel.getRed(data) < 100) ? 0 : rData
				// / 3);
				dImg.setRGB(w, h, (rData < 136) ? 255 : 0);
				lumArray[clrModel.getRed(data)]++;
			}
		}
		StringBuilder lumSb = new StringBuilder();
		StringBuilder pSb = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		int total = raster.getHeight() * raster.getWidth();
		for (int i = 0; i < lumArray.length; i++) {
			if (lumArray[i] < 1) {
				continue;
			}
			lumSb.append(i + " ");
			sb.append(lumArray[i] + " ");
			float pLum = (float) ((lumArray[i] * 1D) / total * 100);
			pSb.append(pLum + "% ");
		}
		System.out.println("---:" + total);
		System.out.println(lumSb.toString());
		System.out.println(sb.toString());
		System.out.println(pSb.toString());
		ImageIO.write(dImg, "jpg", new File("src/test/resources/img/ancc" + "/42R6.jpg"));
	}
}
