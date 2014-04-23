package com.lezo.iscript.ocr.asprise;

/*
 * $Id$
 * 
 */
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.asprise.util.ocr.OCR;

public class Demo {
	public static void main(String[] args) throws IOException {
		if (("1.4").compareTo(System.getProperty("java.vm.version")) > 0) {
			System.err.println("Warining: \n\nYou need Java version 1.4 or above for ImageIO to run this demo.");
			System.err.println("Your current Java version is: " + System.getProperty("java.vm.version"));
			System.err.println("\nSolutions: \n");
			System.err.println("(1) Download JRE/JDK version 1.4 or above; OR \n");
			System.err.println("(2) Run DemoUI, which can run on your current Java virtual machine.");
			System.err.println("    Double click the 'runDemoUI' to invoke it.\n");
			return;
		}
		System.out.println("Welcome to Asprise OCR v4.0 Demo!\n");

		File file = new File("r42l.jpg");

		System.out.println("Trying to perform OCR on image: " + file.getAbsolutePath());

		BufferedImage image = ImageIO.read(file);
		image = doFilterWrite(image);
		String s = new OCR().recognizeEverything(image);
		System.out.println("\n---- RESULTS: ------- \n" + s);
	}

	public static BufferedImage doFilterWrite(BufferedImage image) {
		com.jhlabs.image.NoiseFilter noiseFilter = new com.jhlabs.image.NoiseFilter();
		BufferedImage destImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		noiseFilter.filter(image, destImage);
		try {
			File destFile = new File("filters/noiseFilter.jpg");
			System.out.println(destFile);
			ImageIO.write(destImage, "jpg", destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destImage;
	}

}
