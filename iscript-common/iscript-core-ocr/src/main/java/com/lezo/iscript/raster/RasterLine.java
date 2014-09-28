package com.lezo.iscript.raster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RasterLine {
	private Set<RasterDot> dotSet = new HashSet<RasterDot>();

	public void addElement(RasterDot dot) {
		dotSet.add(dot);
	}

	// 度数
	public double getAngle() {
		List<RasterDot> dotList = new ArrayList<RasterDot>(dotSet);
		RasterDot dot = dotList.get(0);
		RasterDot lastDot = dotList.get(dotList.size() - 1);
		double degree = 0;
		int xx = Math.abs(dot.getX() - lastDot.getX());
		int yy = Math.abs(dot.getY() - lastDot.getY());
		if (yy == 0) {
			degree = 90;
		} else {
			double tan = xx / yy;
			degree = Math.atan(tan);
		}
		return degree;
	}

	public double length() {
		double direct = 0;
		return direct;
	}

}
