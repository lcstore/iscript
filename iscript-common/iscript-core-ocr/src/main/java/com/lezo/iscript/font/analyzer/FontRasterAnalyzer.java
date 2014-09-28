package com.lezo.iscript.font.analyzer;

import java.util.ArrayList;
import java.util.List;

import com.lezo.iscript.font.FontRaster;

public class FontRasterAnalyzer implements FontRasterAnalyzable {
	@Override
	public void doAnalyze(FontRaster fontRaster) {
		List<FontRasterAnalyzable> analyzables = new ArrayList<FontRasterAnalyzable>();
		analyzables.add(new FontRangeAnalyzer());
		analyzables.add(new FontLineAnalyzer());
//		analyzables.add(new FontFilterAnalyzer());
		analyzables.add(new FontVectorAnalyzer());
		for (FontRasterAnalyzable analyzer : analyzables) {
			analyzer.doAnalyze(fontRaster);
		}
	}

}
