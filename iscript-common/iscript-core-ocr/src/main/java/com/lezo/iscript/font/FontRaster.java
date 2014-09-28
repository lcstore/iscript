package com.lezo.iscript.font;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FontRaster {
	private List<FontDot> dotList;
	private List<FontLine> lineList;
	private List<FontVector> vectorList;
	private Rectangle range;

	public FontRaster(List<FontDot> dotList) {
		super();
		this.dotList = dotList;
	}

	public List<FontDot> getDotList() {
		return dotList;
	}

	public void setDotList(List<FontDot> dotList) {
		this.dotList = dotList;
	}

	public List<FontLine> getLineList() {
		return lineList;
	}

	public void setLineList(List<FontLine> lineList) {
		this.lineList = lineList;
	}

	public List<FontVector> getVectorList() {
		return vectorList;
	}

	public void setVectorList(List<FontVector> vectorList) {
		this.vectorList = vectorList;
	}

	public Rectangle getRange() {
		return range;
	}

	public void setRange(Rectangle range) {
		this.range = range;
	}

}
