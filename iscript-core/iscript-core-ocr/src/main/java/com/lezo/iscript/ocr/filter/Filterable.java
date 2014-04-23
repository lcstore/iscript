package com.lezo.iscript.ocr.filter;

import java.awt.image.Raster;

public interface Filterable {
	Raster doFilter(Raster raster);
}
