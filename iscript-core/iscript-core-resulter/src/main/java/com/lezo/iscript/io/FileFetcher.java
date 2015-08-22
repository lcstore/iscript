package com.lezo.iscript.io;

import java.util.concurrent.atomic.AtomicLong;

import com.lezo.iscript.IoGain;
import com.lezo.iscript.IoSeed;
import com.lezo.iscript.parser.FileParser;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;

public class FileFetcher implements IFetcher {
	private static AtomicLong count = new AtomicLong();

	@Override
	public IoGain doFetch(IoSeed seed) throws Exception {
		ClientRest clientRest = ClientRestFactory.getInstance().get(seed.getBucket(), seed.getDomain());
		if (clientRest == null) {
			return null;
		}
		count.incrementAndGet();
		Object result = clientRest.getRester().download(seed.getDataPath());
		IoGain ioGain = new IoGain();
		ioGain.setValue(result);
		ioGain.setIoSeed(seed);
		ioGain.setParser(new FileParser());
		return ioGain;
	}

	public static AtomicLong getCount() {
		return count;
	}
}
