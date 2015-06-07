package com.lezo.iscript.yeam.resultmgr;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

public class RestListFileTest {

	public static void main(String[] args) throws Exception {
		File curFile = new File("logs");
		Date fromDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(fromDate);
		c.add(Calendar.DAY_OF_MONTH, -1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		final long fromStamp = c.getTimeInMillis();
		File[] fileList = curFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".txt") && pathname.lastModified() >= fromStamp;
			}
		});
		Map<String, Integer> pathCountMap = new HashMap<String, Integer>();
		for (File file : fileList) {
			List<String> dataList = FileUtils.readLines(file, "UTF-8");
			for (String data : dataList) {
				if (data.indexOf("iscript") > 0) {
					Integer count = pathCountMap.get(data);
					if (count == null) {
						pathCountMap.put(data, 1);
					} else {
						pathCountMap.put(data, count + 1);
					}
				}
			}
		}
		for (Entry<String, Integer> entry : pathCountMap.entrySet()) {
			if (entry.getValue() > 2) {
				System.out.println(entry);
			}
		}
	}
}
