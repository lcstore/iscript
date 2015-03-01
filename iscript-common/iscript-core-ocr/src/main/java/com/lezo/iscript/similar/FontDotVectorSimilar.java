package com.lezo.iscript.similar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lezo.iscript.font.FontVector;
import com.lezo.iscript.font.utils.FontVectorUtils;

public class FontDotVectorSimilar implements Similarable {
	private FontVector one;
	private FontVector other;

	public FontDotVectorSimilar(FontVector one, FontVector other) {
		super();
		this.one = one;
		this.other = other;
	}

	@Override
	public double getSimilarity() {
		double result = 0;
		Map<String, Double> vScoreMap = new HashMap<String, Double>();
		for (Point pt : FontVectorUtils.getDirectSet()) {
			String key = pt.x + "," + pt.y;
			// TODO
			Double len = 0D;
			Double oLen = 0D;
			Double max = len;
			Double min = oLen;
			if (max < min) {
				max = oLen;
				min = len;
			}
			double score = max.equals(0D) ? 1 : isClose(min / max) ? 1 : 0;
			vScoreMap.put(key, score);
		}
		if (!vScoreMap.isEmpty()) {
			List<Entry<String, Double>> scoreList = new ArrayList<Entry<String, Double>>(vScoreMap.entrySet());
			// doAssort(scoreList);
			double scoreSum = 0;
			for (int i = 0; i < scoreList.size(); i++) {
				scoreSum += scoreList.get(i).getValue();
			}
			result = scoreSum / scoreList.size();
		}
		return result;
	}

	public boolean isInteger(double value) {
		Integer intValue = (int) value;
		Double zero = 0D;
		return zero.equals(value - intValue);
	}

	public boolean isClose(double value) {
		return (1 <= value && value <= 1.5);
	}

	private void doAssort(List<Entry<String, Double>> scoreList) {
		Collections.sort(scoreList, new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

	}

}
