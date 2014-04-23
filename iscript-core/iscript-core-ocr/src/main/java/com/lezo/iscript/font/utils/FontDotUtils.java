package com.lezo.iscript.font.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.lezo.iscript.font.FontDot;

public class FontDotUtils {
	public static FontDot getLastLineDot(FontDot dot, int xOffset, int yOffset, Map<String, FontDot> xyMap) {
		FontDot nextDot = dot;
		while (true) {
			String key = (nextDot.getPt().x + xOffset) + "," + (nextDot.getPt().y + yOffset);
			FontDot getDot = xyMap.get(key);
			if (getDot != null) {
				nextDot = getDot;
			} else {
				break;
			}
		}
		return nextDot;
	}

	public static double getDistance(FontDot aDot, FontDot bDot) {
		// A(X1,Y1), B(X2,Y2),|AB| = 根号[（X1-X2)^2 + (Y1-Y2)^2]
		double xPower = aDot.getPt().x - bDot.getPt().x;
		xPower = xPower * xPower;
		double yPower = aDot.getPt().y - bDot.getPt().y;
		yPower = yPower * yPower;
		return Math.sqrt(xPower + yPower);
	}

	public static Set<FontDot> getOutShapeDots(List<FontDot> dotList) {
		Set<FontDot> outDotSet = new HashSet<FontDot>();
		Map<Integer, List<FontDot>> xMap = new HashMap<Integer, List<FontDot>>();
		Map<Integer, List<FontDot>> yMap = new HashMap<Integer, List<FontDot>>();
		for (FontDot dot : dotList) {
			List<FontDot> xList = xMap.get(dot.getPt().x);
			if (xList == null) {
				xList = new ArrayList<FontDot>();
				xMap.put(dot.getPt().x, xList);
			}
			xList.add(dot);

			List<FontDot> yList = yMap.get(dot.getPt().y);
			if (yList == null) {
				yList = new ArrayList<FontDot>();
				yMap.put(dot.getPt().y, yList);
			}
			yList.add(dot);
		}
		for (Entry<Integer, List<FontDot>> entry : xMap.entrySet()) {
			List<FontDot> dots = entry.getValue();
			doYAssort(dots);
			outDotSet.add(dots.get(0));
			outDotSet.add(dots.get(dots.size() - 1));
		}
		for (Entry<Integer, List<FontDot>> entry : yMap.entrySet()) {
			List<FontDot> dots = entry.getValue();
			doXAssort(dots);
			outDotSet.add(dots.get(0));
			outDotSet.add(dots.get(dots.size() - 1));
		}
		return outDotSet;
	}

	public static void doXAssort(List<FontDot> dots) {
		Collections.sort(dots, new Comparator<FontDot>() {
			@Override
			public int compare(FontDot arg0, FontDot arg1) {
				return arg0.getPt().x - arg1.getPt().x;
			}
		});
	}

	public static void doYAssort(List<FontDot> dots) {
		Collections.sort(dots, new Comparator<FontDot>() {
			@Override
			public int compare(FontDot arg0, FontDot arg1) {
				return arg0.getPt().y - arg1.getPt().y;
			}
		});
	}
}
