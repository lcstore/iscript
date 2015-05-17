package com.lezo.iscript.font.analyzer;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.lezo.iscript.font.FontDot;
import com.lezo.iscript.font.FontRaster;
import com.sun.jmx.remote.internal.ArrayQueue;

public class FontRangeAnalyzer implements FontRasterAnalyzable {
	@Override
	public void doAnalyze(FontRaster fontRaster) {
		List<FontDot> dotList = fontRaster.getDotList();
		if (dotList.isEmpty()) {
			return;
		}
		int size = dotList.size();
		FontDot dot = dotList.get(0);
		Point leftUpPt = dot.getPt();
		Point rightDownPt = dot.getPt();
		for (int i = 1; i < size; i++) {
			dot = dotList.get(i);
			if (leftUpPt.x > dot.getPt().x) {
				leftUpPt.x = dot.getPt().x;
			} else if (rightDownPt.x < dot.getPt().x) {
				rightDownPt.x = dot.getPt().x;
			}
			if (leftUpPt.y > dot.getPt().y) {
				leftUpPt.y = dot.getPt().y;
			} else if (rightDownPt.y < dot.getPt().y) {
				rightDownPt.y = dot.getPt().y;
			}
		}
		int width = rightDownPt.x - leftUpPt.x + 1;
		int height = rightDownPt.y - leftUpPt.y + 1;
		Rectangle range = new Rectangle(leftUpPt.x, leftUpPt.y, width, height);
		fontRaster.setRange(range);

		Set<Point> ptSet = new HashSet<Point>();
		for (FontDot mDot : dotList) {
			ptSet.add(new Point(mDot.getPt().x, mDot.getPt().y));
		}

		Set<Point> linkPtSet = new HashSet<Point>();
		Queue<Point> ptQueue = new LinkedList<Point>();
		for (int y = 0; y < range.height; y++) {
			for (int x = 0; x < range.width; x++) {
				Point pt = new Point(x + range.x, y + range.y);
				if (ptSet.contains(pt)) {
					ptQueue.offer(pt);
					linkStrokes(ptQueue, ptSet, linkPtSet);
					break;
				}
			}
		}
		for (int y = 0; y < range.height; y++) {
			for (int x = 0; x < range.width; x++) {
				Point pt = new Point(x + range.x, y + range.y);
				if (linkPtSet.contains(pt)) {
					System.out.print("9 ");
				} else {
					System.out.print("0 ");
				}
			}
			System.out.println("");
		}
	}

	private void linkStrokes(Queue<Point> ptQueue, Set<Point> ptSet, Set<Point> linkPtSet) {
		List<Point> directList = getDirectList();
		while (!ptQueue.isEmpty()) {
			Point pt = ptQueue.poll();
			linkPtSet.add(pt);
			for (Point direct : directList) {
				Point foundPt = linkeDirectStrokes(pt, direct, ptSet);
				if (foundPt != null) {
					ptQueue.offer(foundPt);
				}
			}
		}
	}

	private Point linkeDirectStrokes(Point pt, Point direct, Set<Point> ptSet) {
		Point findPt = pt;
		Point nextPt = new Point();
		while (true) {
			nextPt.x = findPt.x + direct.x;
			nextPt.y = findPt.y + direct.y;
			if (ptSet.contains(nextPt)) {
				findPt = nextPt;
			} else {
				break;
			}
		}
		return findPt.equals(pt) ? null : findPt;
	}

	public List<Point> getDirectList() {
		List<Point> directList = new ArrayList<Point>();
		directList.add(new Point(1, 0));
		directList.add(new Point(0, 1));
//		directList.add(new Point(-1, -1));
//		directList.add(new Point(1, 1));
//		directList.add(new Point(-1, 1));
//		directList.add(new Point(-1, 0));
//		directList.add(new Point(0, -1));
		return directList;
	}

}
