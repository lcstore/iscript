package com.lezo.iscript.io;

import java.util.concurrent.atomic.AtomicLong;

import com.lezo.iscript.IoGain;
import com.lezo.iscript.IoSeed;
import com.lezo.iscript.parser.PathParser;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;
import com.lezo.rest.data.RestList;

public class PathFetcher implements IFetcher {
	private static AtomicLong count = new AtomicLong();

	@Override
	public IoGain doFetch(IoSeed seed) throws Exception {
		ClientRest clientRest = ClientRestFactory.getInstance().get(seed.getBucket(), seed.getDomain());
		if (clientRest == null) {
			return null;
		}
		count.incrementAndGet();
		RestList result = clientRest.getRester().listFiles(seed.getDataPath(), seed.getParams());
		IoGain ioGain = new IoGain();
		ioGain.setValue(result);
		ioGain.setIoSeed(seed);
		ioGain.setParser(new PathParser());
		return ioGain;
	}

	public static AtomicLong getCount() {
		return count;
	}
}
