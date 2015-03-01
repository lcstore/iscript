package com.lezo.iscript.raster;

import java.util.HashSet;
import java.util.Set;

public class RasterFont {
	private Set<RasterLine> lineSet = new HashSet<RasterLine>();

	public void addElement(RasterLine line) {
		lineSet.add(line);
	}
}
