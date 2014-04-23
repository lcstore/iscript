package com.lezo.iscript.font.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.lezo.iscript.font.FontDot;
import com.lezo.iscript.font.FontLine;
import com.lezo.iscript.font.FontRaster;
import com.lezo.iscript.font.FontVector;
import com.lezo.iscript.font.utils.FontRasterUtils;

public class FontVectorAnalyzer implements FontRasterAnalyzable {

	@Override
	public void doAnalyze(FontRaster fontRaster) {
		Map<FontDot, Set<FontLine>> dotLineMap = FontRasterUtils.getDot2LineMap(fontRaster);
		Set<FontDot> coreDots = getCoreDots(dotLineMap);
		List<FontVector> vectorList = new ArrayList<FontVector>();
		for (FontDot core : coreDots) {
			Set<FontLine> lineSet = dotLineMap.get(core);
			FontVector vector = new FontVector(core, lineSet);
			vectorList.add(vector);
		}
		fontRaster.setVectorList(vectorList);
	}

	private Set<FontDot> getCoreDots(Map<FontDot, Set<FontLine>> dotLineMap) {
		Set<FontDot> dotSet = new HashSet<FontDot>();
		Set<FontLine> lineSet = new HashSet<FontLine>();
		for (Entry<FontDot, Set<FontLine>> entry : dotLineMap.entrySet()) {
			if (entry.getValue().size() >= 2) {
				dotSet.add(entry.getKey());
			}
			lineSet.addAll(lineSet);
		}
		for (FontLine line : lineSet) {
			dotSet.add(line.getStartDot());
			dotSet.add(line.getEndDot());
		}
		return dotSet;
	}
}
