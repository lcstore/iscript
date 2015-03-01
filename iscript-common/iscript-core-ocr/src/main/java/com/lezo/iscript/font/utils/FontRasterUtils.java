package com.lezo.iscript.font.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lezo.iscript.font.FontDot;
import com.lezo.iscript.font.FontLine;
import com.lezo.iscript.font.FontRaster;

public class FontRasterUtils {

	public static Map<String, FontDot> getCoordMap(List<FontDot> dotList) {
		Map<String, FontDot> coordMap = new HashMap<String, FontDot>();
		for (FontDot dot : dotList) {
			String key = getCoordKey(dot, 0, 0);
			coordMap.put(key, dot);
		}
		return coordMap;
	}

	public static String getCoordKey(FontDot dot, int xOffset, int yOffset) {
		return (dot.getPt().x + xOffset) + "," + (dot.getPt().y + yOffset);
	}

	public static Map<FontDot, Set<FontLine>> getDot2LineMap(FontRaster fontRaster) {
		Map<FontDot, Set<FontLine>> dot2LineMap = new HashMap<FontDot, Set<FontLine>>();
		List<FontDot> dotList = fontRaster.getDotList();
		List<FontLine> lineList = fontRaster.getLineList();
		if (dotList == null || lineList == null) {
			return dot2LineMap;
		}
		for (FontDot dot : dotList) {
			FontDot key = dot;
			Set<FontLine> lineSet = dot2LineMap.get(key);
			if (lineSet == null) {
				lineSet = new HashSet<FontLine>();
				dot2LineMap.put(key, lineSet);
			}
			for (FontLine line : lineList) {
				if (line.contains(dot)) {
					lineSet.add(line);
				}
			}
		}
		return dot2LineMap;
	}

	public static Map<FontLine, Set<FontDot>> getLine4DotMap(FontRaster fontRaster) {
		Map<FontLine, Set<FontDot>> line4DotMap = new HashMap<FontLine, Set<FontDot>>();
		List<FontDot> dotList = fontRaster.getDotList();
		List<FontLine> lineList = fontRaster.getLineList();
		if (dotList == null || lineList == null) {
			return line4DotMap;
		}
		for (FontLine line : lineList) {
			FontLine key = line;
			Set<FontDot> lineSet = line4DotMap.get(key);
			if (lineSet == null) {
				lineSet = new HashSet<FontDot>();
				line4DotMap.put(key, lineSet);
			}
			for (FontDot dot : dotList) {
				if (line.contains(dot)) {
					lineSet.add(dot);
				}
			}
		}
		return line4DotMap;
	}

	public static Map<Double, List<FontLine>> getParallelLineMap(FontRaster fontRaster) {
		Map<Double, List<FontLine>> lineMap = new HashMap<Double, List<FontLine>>();
		List<FontLine> lineList = fontRaster.getLineList();
		for (FontLine line : lineList) {
			Double key = line.getSlope();
			List<FontLine> pLineList = lineMap.get(key);
			if (pLineList == null) {
				pLineList = new ArrayList<FontLine>();
				lineMap.put(key, pLineList);
			}
			pLineList.add(line);
		}
		return lineMap;
	}

	public static double getDotLineDist(FontLine line, FontDot dot) {
		// │AXo＋BYo＋C│／√（A²＋B²）
		double up = Math.abs(line.getArgA() * dot.getPt().x + line.getArgB() * dot.getPt().y + line.getArgC());
		double down = Math.sqrt(line.getArgA() * line.getArgA() + line.getArgB() * line.getArgB());
		return up / down;
	}
}
