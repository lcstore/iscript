package com.lezo.iscript.font.utils;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class FontVectorUtils {
	private static Set<Point> directSet = new HashSet<Point>();
	static {
		directSet.add(new Point(-1, -1));
		directSet.add(new Point(0, -1));
		directSet.add(new Point(1, -1));
		directSet.add(new Point(1, 0));
		directSet.add(new Point(1, 1));
		directSet.add(new Point(0, 1));
		directSet.add(new Point(-1, 1));
		directSet.add(new Point(-1, 0));
	}

	public static Set<Point> getDirectSet() {
		return directSet;
	}

}
