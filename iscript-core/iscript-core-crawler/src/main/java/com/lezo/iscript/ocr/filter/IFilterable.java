package com.lezo.iscript.ocr.filter;

import java.awt.image.BufferedImage;

public interface IFilterable {
	BufferedImage doFilter(BufferedImage source);
}
