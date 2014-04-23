package com.lezo.iscript.font.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lezo.iscript.font.FontDot;
import com.lezo.iscript.font.FontLine;
import com.lezo.iscript.font.FontRaster;
import com.lezo.iscript.font.utils.FontRasterUtils;

public class FontLineAnalyzer implements FontRasterAnalyzable {

	@Override
	public void doAnalyze(FontRaster fontRaster) {
		List<FontDot> dotList = fontRaster.getDotList();
		Set<FontLine> lineSet = new HashSet<FontLine>();
		Map<String, FontDot> coordMap = FontRasterUtils.getCoordMap(dotList);
		for (FontDot dot : dotList) {
			addLine(dot, 0, 1, coordMap, lineSet);
			addLine(dot, 1, 0, coordMap, lineSet);
			addLine(dot, -1, -1, coordMap, lineSet);
			addLine(dot, -1, 1, coordMap, lineSet);
		}
		fontRaster.setLineList(new ArrayList<FontLine>(lineSet));
	}

	private void addLine(FontDot dot, int xOffset, int yOffset, Map<String, FontDot> coordMap, Set<FontLine> lineSet) {
		// 非端点不用去找线
		if (inLineDot(dot, xOffset, yOffset, coordMap)) {
			return;
		}
		FontDot endDot = getLineEndDot(dot, xOffset, yOffset, coordMap);
		FontDot reverseEndDot = getLineEndDot(dot, -xOffset, -yOffset, coordMap);
		if (endDot != reverseEndDot) {
			FontLine line = FontLine.getFontLine(endDot, reverseEndDot);
			lineSet.add(line);
		}
	}

	/**
	 * 点在线内
	 * 
	 * @param dot
	 * @param xOffset
	 * @param yOffset
	 * @param coordMap
	 * @return
	 */
	private boolean inLineDot(FontDot dot, int xOffset, int yOffset, Map<String, FontDot> coordMap) {
		String key = (dot.getPt().x + xOffset) + "," + (dot.getPt().y + yOffset);
		String reversekey = (dot.getPt().x - xOffset) + "," + (dot.getPt().y - yOffset);
		return coordMap.containsKey(key) && coordMap.containsKey(reversekey);
	}

	private FontDot getLineEndDot(FontDot dot, int xOffset, int yOffset, Map<String, FontDot> coordMap) {
		String key = (dot.getPt().x + xOffset) + "," + (dot.getPt().y + yOffset);
		FontDot endDot = coordMap.get(key);
		if (endDot != null) {
			return getLineEndDot(endDot, xOffset, yOffset, coordMap);
		}
		return dot;
	}
}
