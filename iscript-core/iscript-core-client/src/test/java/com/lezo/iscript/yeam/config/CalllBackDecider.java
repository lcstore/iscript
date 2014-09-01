package com.lezo.iscript.yeam.config;

import java.util.HashMap;
import java.util.Map;

import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class CalllBackDecider implements ConfigParser {
	private Map<String, Retainer> deciderMap = new HashMap<String, Retainer>();

	public CalllBackDecider() {
		deciderMap.put("ConfigYhdProduct", new CalllBackDecider.NoneRetainer());
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		ResultWritable rWritable = (ResultWritable) task.get("ResultWritable");
		Retainer decider = getDecider(rWritable);
		return decider == null ? rWritable.getResult() : decider.getCallBack(rWritable);
	}

	private Retainer getDecider(ResultWritable rWritable) {
		return null;
	}

	interface Retainer {
		String getCallBack(ResultWritable rWritable);
	}

	class NoneRetainer implements Retainer {
		@Override
		public String getCallBack(ResultWritable rWritable) {
			return null;
		}

	}
}
