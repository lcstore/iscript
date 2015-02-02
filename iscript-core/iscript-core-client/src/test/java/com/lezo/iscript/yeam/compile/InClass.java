package com.lezo.iscript.yeam.compile;

import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class InClass implements ConfigParser {

	@Override
	public String getName() {
		return "InClass.type";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				System.out.println("InClass.run..");
			}
		};
		runnable.run();
		return null;
	}

}
