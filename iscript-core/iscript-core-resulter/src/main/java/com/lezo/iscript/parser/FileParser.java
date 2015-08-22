package com.lezo.iscript.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.log4j.Log4j;

import com.lezo.iscript.IoGain;
import com.lezo.iscript.io.IoWatcher;
import com.lezo.iscript.yeam.resultmgr.listener.ConsumeListener;
import com.lezo.iscript.yeam.resultmgr.listener.ConsumeListenerManager;

@Log4j
public class FileParser implements IParser {
	private static AtomicLong count = new AtomicLong();

	@Override
	public void doParse(IoGain ioGain) {
		String type = ioGain.getIoSeed().getType();
		List<String> lineList = toLines(ioGain);
		String key = ioGain.getIoSeed().toKey();
		count.getAndAdd(lineList.size());
		// String dataString = ioGain.getValue().toString();
		// log.info("data.key:" + ioGain.getIoSeed().toKey() + ",value:" +
		// dataString);
		for (String line : lineList) {
			long start = System.currentTimeMillis();
			doConsume(type, line);
			long cost = System.currentTimeMillis() - start;
			if (cost > 60000) {
				log.warn("key:" + key + ",parse too long.cost:" + cost);
			}
		}

		IoWatcher.getInstance().addFile(ioGain.getIoSeed());

	}

	private void doConsume(String type, String data) {
		Iterator<Entry<String, ConsumeListener>> it = ConsumeListenerManager.getInstance().iterator();
		while (it.hasNext()) {
			Entry<String, ConsumeListener> entry = it.next();
			entry.getValue().doConsume(type, data);
		}
	}

	private List<String> toLines(IoGain ioGain) {
		if (ioGain.getValue() == null) {
			return java.util.Collections.emptyList();
		}
		StringTokenizer tokenizer = new StringTokenizer(ioGain.getValue().toString(), "\n");
		List<String> lineList = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
			lineList.add(tokenizer.nextElement().toString());
		}
		return lineList;
	}

	public static AtomicLong getCount() {
		return count;
	}

}
