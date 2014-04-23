package com.lezo.iscript.similar;

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
import com.lezo.iscript.font.FontVector;
import com.lezo.iscript.font.utils.FontRasterUtils;

public class FontRasterSimilar implements Similarable {
	private FontRaster refer;
	private FontRaster other;

	public FontRasterSimilar(FontRaster refer, FontRaster other) {
		super();
		this.refer = refer;
		this.other = other;
	}

	@Override
	public double getSimilarity() {
		double result = 0;
		List<FontLine> rLineList = refer.getLineList();
		List<FontLine> oLineList = other.getLineList();
		descLenLine(rLineList);
		descLenLine(oLineList);

		Map<FontLine, Map<FontDot, Double>> lineDotDistMap = getLineDotDistMap(rLineList, refer.getDotList());
		Map<FontLine, Map<FontDot, Double>> oLineDotDistMap = getLineDotDistMap(oLineList, other.getDotList());
		Map<FontDot, FontVector> rDVMap = getDotVectMap(refer);
		Map<FontDot, FontVector> oDVMap = getDotVectMap(other);

		Map<FontLine, Map<FontLine, Double>> referOtherLineScoreMap = new HashMap<FontLine, Map<FontLine, Double>>();
		double minSimilar = 0.5;
		for (FontLine rLine : rLineList) {
			Map<FontLine, Double> newLineScoreMap = new HashMap<FontLine, Double>();
			referOtherLineScoreMap.put(rLine, newLineScoreMap);
			for (FontLine oLine : oLineList) {
				double similar = getLineSimilar(rLine, oLine, rDVMap, oDVMap);
				if (similar >= minSimilar) {
					newLineScoreMap.put(oLine, similar);
				}
			}
		}
		//
		Map<FontLine, List<Entry<FontLine, Double>>> lineMostSimilarMap = getMostSimilarMap(referOtherLineScoreMap);
		Map<FontLine, List<Entry<FontDot, Double>>> dotDistMap = getDescLineDotDistMap(lineDotDistMap);
		Map<FontLine, List<Entry<FontDot, Double>>> oDotDistMap = getDescLineDotDistMap(oLineDotDistMap);
		doTriangleSimilar(lineMostSimilarMap, dotDistMap, oDotDistMap);
		return result;

	}

	private Map<FontLine, List<Entry<FontDot, Double>>> getDescLineDotDistMap(
			Map<FontLine, Map<FontDot, Double>> lineDotDistMap) {
		Map<FontLine, List<Entry<FontDot, Double>>> distEntryMap = new HashMap<FontLine, List<Entry<FontDot, Double>>>();
		for (Entry<FontLine, Map<FontDot, Double>> entry : lineDotDistMap.entrySet()) {
			List<Entry<FontDot, Double>> entryList = new ArrayList<Entry<FontDot, Double>>(entry.getValue().entrySet());
			descDistEntry(entryList);
			distEntryMap.put(entry.getKey(), entryList);
		}
		return distEntryMap;
	}

	private void descDistEntry(List<Entry<FontDot, Double>> entryList) {
		Collections.sort(entryList, new Comparator<Entry<FontDot, Double>>() {

			@Override
			public int compare(Entry<FontDot, Double> o1, Entry<FontDot, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

	}

	private void doTriangleSimilar(Map<FontLine, List<Entry<FontLine, Double>>> lineMostSimilarMap,
			Map<FontLine, List<Entry<FontDot, Double>>> dotDistMap,
			Map<FontLine, List<Entry<FontDot, Double>>> oDotDistMap) {

		// 相似三角形，相似度
		for (Entry<FontLine, List<Entry<FontLine, Double>>> entry : lineMostSimilarMap.entrySet()) {
			List<Entry<FontDot, Double>> dotList = dotDistMap.get(entry.getKey());
			for (Entry<FontLine, Double> oLineEntry : entry.getValue()) {
				List<Entry<FontDot, Double>> oDotList = oDotDistMap.get(oLineEntry.getKey());
				double score = getTriangleSimilarScore(entry.getKey(), dotList, oLineEntry.getKey(), oDotList);
			}
		}

	}

	private double getTriangleSimilarScore(FontLine refer, List<Entry<FontDot, Double>> dotList, FontLine other,
			List<Entry<FontDot, Double>> oDotList) {
		return 0;
	}

	private Map<FontLine, List<Entry<FontLine, Double>>> getMostSimilarMap(Map<FontLine, Map<FontLine, Double>> rosMap) {
		Map<FontLine, List<Entry<FontLine, Double>>> map = new HashMap<FontLine, List<Entry<FontLine, Double>>>(
				rosMap.size());
		for (Entry<FontLine, Map<FontLine, Double>> entry : rosMap.entrySet()) {
			List<Entry<FontLine, Double>> lsList = new ArrayList<Entry<FontLine, Double>>(entry.getValue().entrySet());
			descScoreEntry(lsList);
			map.put(entry.getKey(), lsList);
		}
		return null;
	}

	private void descScoreEntry(List<Entry<FontLine, Double>> lsList) {
		Collections.sort(lsList, new Comparator<Entry<FontLine, Double>>() {
			@Override
			public int compare(Entry<FontLine, Double> o1, Entry<FontLine, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
	}

	private Map<FontDot, FontVector> getDotVectMap(FontRaster raster) {
		Map<FontDot, FontVector> dvMap = new HashMap<FontDot, FontVector>();
		for (FontVector v : raster.getVectorList()) {
			dvMap.put(v.getDot(), v);
		}
		return dvMap;
	}

	private double getLineSimilar(FontLine refer, FontLine other, Map<FontDot, FontVector> rDVMap,
			Map<FontDot, FontVector> oDVMap) {
		// TODO: 未考虑旋转
		double pScore = refer.isParallel(other) ? 1 : 0;
		double sScore = getVectSimilar(rDVMap.get(refer.getStartDot()), oDVMap.get(other.getStartDot()));
		double eScore = getVectSimilar(rDVMap.get(refer.getEndDot()), oDVMap.get(other.getEndDot()));
		return pScore * 0.4 + sScore * 0.3 + eScore * 0.3;
	}

	private double getVectSimilar(FontVector refer, FontVector other) {
		if (refer == null || other == null) {
			return 0;
		}
		// TODO: 未考虑旋转
		Map<FontLine, FontLine> sLineMap = new HashMap<FontLine, FontLine>();
		Map<FontLine, Double> sDistMap = new HashMap<FontLine, Double>();
		for (FontLine rLine : refer.getLineSet()) {
			double rLen = rLine.getLen();
			for (FontLine oLine : other.getLineSet()) {
				if (!rLine.isParallel(oLine)) {
					continue;
				}
				double dist = Math.abs(rLen - oLine.getLen());
				FontLine oCurLine = sLineMap.get(rLine);
				if (oCurLine == null) {
					sLineMap.put(rLine, oLine);
					sDistMap.put(rLine, dist);
				} else {
					double curDist = sDistMap.get(rLine);
					if (dist < curDist) {
						sLineMap.put(rLine, oLine);
						sDistMap.put(rLine, dist);
					}
				}
			}
		}
		return sDistMap.size() * 1D / refer.getLineSet().size();
	}

	private void descLenLine(List<FontLine> lines) {
		Collections.sort(lines, new Comparator<FontLine>() {
			@Override
			public int compare(FontLine left, FontLine right) {
				return (int) (right.getLen() - left.getLen());
			}
		});
	}

	private Map<FontLine, Map<FontDot, Double>> getLineDotDistMap(List<FontLine> lines, List<FontDot> dotList) {
		Map<FontLine, Map<FontDot, Double>> lineDotDistMap = new HashMap<FontLine, Map<FontDot, Double>>();
		for (FontLine line : lines) {
			Map<FontDot, Double> distMap = new HashMap<FontDot, Double>();
			lineDotDistMap.put(line, distMap);
			for (FontDot dot : dotList) {
				double dist = FontRasterUtils.getDotLineDist(line, dot);
				distMap.put(dot, dist);
			}
		}
		return lineDotDistMap;
	}

}
