package com.lezo.iscript.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import org.junit.Test;

public class FontSpot {

	@Test
	public void testFontSpot() {
		String string = "123.字体识别";
		GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (Font font : gEnv.getAllFonts()) {
			AffineTransform tx = new AffineTransform();
			FontRenderContext frc = new FontRenderContext(tx, true, true);
			System.out.println(font+","+font.getMaxCharBounds(frc));
		}
	}
}
