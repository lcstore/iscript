package com.lezo.iscript.ocr.histogram;

public class HistogramAssort {

	public static int[] doAssort(int[] his, ColorAssorter assorter) {
		int maxSort = assorter.doAssort(his.length);
		int[] destHis = new int[maxSort + 1];
		for (int i = 0; i < his.length; i++) {
			int color = assorter.doAssort(i);
			destHis[color] += his[i];
		}
		return destHis;
	}
}
