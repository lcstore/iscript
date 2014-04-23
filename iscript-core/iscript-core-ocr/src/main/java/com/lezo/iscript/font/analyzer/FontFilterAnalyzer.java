package com.lezo.iscript.font.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.lezo.iscript.font.FontDot;
import com.lezo.iscript.font.FontLine;
import com.lezo.iscript.font.FontRaster;
import com.lezo.iscript.font.utils.FontDotUtils;
import com.lezo.iscript.font.utils.FontRasterUtils;

public class FontFilterAnalyzer implements FontRasterAnalyzable {

	@Override
	public void doAnalyze(FontRaster fontRaster) {
		Map<FontDot, Set<FontLine>> dotLineMap = FontRasterUtils.getDot2LineMap(fontRaster);
		Map<FontLine, Set<FontDot>> line4DotMap = FontRasterUtils.getLine4DotMap(fontRaster);
		Map<Double, List<FontLine>> pLineMap = FontRasterUtils.getParallelLineMap(fontRaster);
		Set<FontLine> keepLineSet = new HashSet<FontLine>();
		for (Entry<Double, List<FontLine>> entry : pLineMap.entrySet()) {
			if (entry.getValue().size() < 2) {
				keepLineSet.addAll(entry.getValue());
				continue;
			}
			List<FontLine> keepLines = filterLine(entry.getValue(), dotLineMap, line4DotMap);
			if (keepLines != null) {
				keepLineSet.addAll(keepLines);
			}
		}
		System.out.println("before:" + fontRaster.getLineList().size() + "," + fontRaster.getLineList());
		fontRaster.setLineList(new ArrayList<FontLine>(keepLineSet));
		System.out.println("after:" + fontRaster.getLineList().size() + "," + fontRaster.getLineList());
	}

	private List<FontLine> filterLine(List<FontLine> pLineList, Map<FontDot, Set<FontLine>> dotLineMap,
			Map<FontLine, Set<FontDot>> line4DotMap) {
		Set<FontLine> keepSet = new HashSet<FontLine>();
		Map<FontLine, List<FontLine>> nearLineMap = getNearLine(pLineList);
		for (Entry<FontLine, List<FontLine>> entry : nearLineMap.entrySet()) {
			List<FontLine> lines = entry.getValue();
			if (lines.size() < 2) {
				for (FontLine line : lines) {
					if (isExclusiveLine(line, line4DotMap, dotLineMap)) {
						keepSet.add(line);
					} else if (isSubsetLine(line, line4DotMap, dotLineMap)) {
					} else {
						keepSet.add(line);
					}
				}
			} else {
				// TODO:是不是最长就OK
				descLenAssort(lines);
				FontLine line = lines.get(0);
				if (isExclusiveLine(line, line4DotMap, dotLineMap)) {
					keepSet.add(line);
				} else if (isSubsetLine(line, line4DotMap, dotLineMap)) {
				} else {
					keepSet.add(line);
				}
			}
		}
		return new ArrayList<FontLine>(keepSet);
	}

	private Map<FontLine, List<FontLine>> getNearLine(List<FontLine> pLineList) {
		Map<FontLine, List<FontLine>> nearLineMap = new HashMap<FontLine, List<FontLine>>();
		for (FontLine refer : pLineList) {
			for (FontLine line : pLineList) {
				if (refer.equals(line) || isNear(refer, line)) {
					List<FontLine> nearList = nearLineMap.get(refer);
					if (nearList == null) {
						nearList = new ArrayList<FontLine>();
						nearLineMap.put(refer, nearList);
					}
					nearList.add(line);
				}
			}
		}
		return nearLineMap;
	}

	/**
	 * 是其他直线的子集
	 * 
	 * @param line
	 * @param line4DotMap
	 * @param dotLineMap
	 * @return
	 */
	private boolean isSubsetLine(FontLine line, Map<FontLine, Set<FontDot>> line4DotMap,
			Map<FontDot, Set<FontLine>> dotLineMap) {
		Set<FontDot> dotSet = line4DotMap.get(line);
		Map<FontLine, Integer> lineHitMap = new HashMap<FontLine, Integer>();
		for (FontDot dot : dotSet) {
			Set<FontLine> lineList = dotLineMap.get(dot);
			for (FontLine hitLine : lineList) {
				Integer hit = lineHitMap.get(hitLine);
				if (hit == null) {
					lineHitMap.put(hitLine, 1);
				} else {
					lineHitMap.put(hitLine, hit + 1);
				}
			}
		}
		Set<FontLine> fullHitSet = new HashSet<FontLine>();
		int fullSize = dotSet.size();
		for (Entry<FontLine, Integer> entry : lineHitMap.entrySet()) {
			if (entry.getValue() == fullSize) {
				fullHitSet.add(line);
			}
		}
		FontLine maxLine = null;
		if (!fullHitSet.isEmpty()) {
			List<FontLine> lineList = new ArrayList<FontLine>(fullHitSet);
			descLenAssort(lineList);
			maxLine = lineList.get(0);
		}
		return !line.equals(maxLine);
	}

	/**
	 * 这条线上的所有点都只属于这条线
	 * 
	 * @param line
	 * @param line4DotMap
	 * @param dotLineMap
	 * @return
	 */
	private boolean isExclusiveLine(FontLine line, Map<FontLine, Set<FontDot>> line4DotMap,
			Map<FontDot, Set<FontLine>> dotLineMap) {
		Set<FontDot> dotSet = line4DotMap.get(line);
		boolean soleOwner = true;
		for (FontDot dot : dotSet) {
			Set<FontLine> lineList = dotLineMap.get(dot);
			if (lineList.size() > 1) {
				soleOwner = false;
				break;
			}
		}
		return soleOwner;
	}

	private boolean isNear(FontLine one, FontLine refer) {
		return (refer.getStartDot().after(one.getStartDot()) && one.getEndDot().after(refer.getEndDot()))
				|| (one.getStartDot().after(refer.getStartDot()) && refer.getEndDot().after(one.getEndDot()));
	}

	private void descLenAssort(List<FontLine> lineList) {
		Collections.sort(lineList, new Comparator<FontLine>() {
			@Override
			public int compare(FontLine o1, FontLine o2) {
				Double dist = FontDotUtils.getDistance(o1.getStartDot(), o1.getEndDot());
				Double dist2 = FontDotUtils.getDistance(o2.getStartDot(), o2.getEndDot());
				return dist2.compareTo(dist);
			}
		});
	}

}
