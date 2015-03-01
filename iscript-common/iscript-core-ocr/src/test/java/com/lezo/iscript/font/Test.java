package com.lezo.iscript.font;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class Test extends JFrame {
	byte[] dotfont;
	BufferedImage imgCH;
	int[] verify = { 128, 64, 32, 16, 8, 4, 2, 1 };
	String test = "点阵汉字的测试";
	int imgWidth = 300;
	int imgHeight = 200;

	public Test() {
		super("DotFont");

		File file = new File("src/test/resources/front/gb.dat");
		try {
			FileInputStream fis = new FileInputStream(file);
			dotfont = new byte[fis.available()];
			fis.read(dotfont);
			fis.close();
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(300, 240);
		this.show();
	}

	void createCH(byte[] ch, int off) {
		int q1 = ch[off] & 0xff;
		int q2 = ch[off + 1] & 0xff;
		int offset = (q1 - 0xa1) * 94 * 24;
		q2 -= 0xa1;
		offset += q2 * 24;
		imgCH = new BufferedImage(12, 12, BufferedImage.TYPE_INT_RGB);
		for (int h = 0; h < 12; h++) {
			byte b = dotfont[offset++];
			for (int w = 0; w < 8; w++) {
				if ((b & verify[w]) == verify[w]) {
					imgCH.setRGB(w, h, 0xffffffff);
				} else {
					imgCH.setRGB(w, h, 0);
				}
			}
			b = dotfont[offset++];
			for (int w = 0; w < 4; w++) {
				if ((b & verify[w]) == verify[w]) {
					imgCH.setRGB(w + 8, h, 0xffffffff);
				} else {
					imgCH.setRGB(w + 8, h, 0);
				}
			}
		}
	}

	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		byte[] an = str2bytes(test);
		int offset = 0;
		int x = 10, y = 34;
		while (y < imgHeight && offset < an.length) {
			int b = an[offset] & 0xff;
			if (b > 0x7f) {
				createCH(an, offset);
				g.drawImage(imgCH, x, y, null);
				x += 12;
				offset += 2;
			} else { // 英文暂时不考虑
				x += 6;
				offset++;
			}
			if (x > imgWidth) {
				x = 10;
				y += 14;
			}
		}
	}

	byte[] str2bytes(String s) {
		if (null == s || "".equals(s)) {
			return null;
		}
		byte[] abytes = null;
		try {
			abytes = s.getBytes("gb2312");
		} catch (UnsupportedEncodingException ex) {
		}
		return abytes;
	}

	public static void main(String[] args) {
		new Test();
	}
}
